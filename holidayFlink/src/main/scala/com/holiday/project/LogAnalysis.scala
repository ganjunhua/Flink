package com.holiday.project

import java.text.SimpleDateFormat
import java.util
import java.util.{Date, Properties}

import org.apache.flink.api.common.functions.RuntimeContext
import org.apache.flink.api.common.serialization.SimpleStringSchema
import org.apache.flink.api.java.tuple.Tuple
import org.apache.flink.streaming.api.scala.StreamExecutionEnvironment
import org.apache.flink.streaming.connectors.kafka.FlinkKafkaConsumer
import org.apache.flink.api.scala._
import org.apache.flink.streaming.api.TimeCharacteristic
import org.apache.flink.streaming.api.functions.AssignerWithPeriodicWatermarks
import org.apache.flink.streaming.api.scala.function.WindowFunction
import org.apache.flink.streaming.api.watermark.Watermark
import org.apache.flink.streaming.api.windowing.time.Time
import org.apache.flink.streaming.api.windowing.windows.TimeWindow
import org.apache.flink.streaming.connectors.elasticsearch.{ElasticsearchSinkFunction, RequestIndexer}
import org.apache.flink.streaming.connectors.elasticsearch6.ElasticsearchSink
import org.apache.flink.util.Collector
import org.apache.http.HttpHost
import org.elasticsearch.action.index.IndexRequest
import org.elasticsearch.client.Requests
import org.slf4j.LoggerFactory

import scala.collection.mutable.ArrayBuffer

object LogAnalysis {
  //在生产中记录日志建议采用这种方式
  val logger = LoggerFactory.getLogger("LogAnalysis")

  def main(args: Array[String]): Unit = {
    val env = StreamExecutionEnvironment.getExecutionEnvironment
    env.setStreamTimeCharacteristic(TimeCharacteristic.EventTime)
    // 创建kafka的配置，用于接收kafka中数据
    val topic = "test"
    val properties = new Properties()
    properties.setProperty("bootstrap.servers", "192.168.226.128:9092")
    properties.setProperty("group.id", "test-group")
    //创建接收者
    val consumer = new FlinkKafkaConsumer[String](topic, new SimpleStringSchema(), properties)
    consumer.setStartFromLatest()
    //接收kafka的数据
    val data = env.addSource(consumer)

    val logData = data.map(x => {
      //拆分数据  数据格式是 scala	CN	M	2019-11-08 16:33:19	127.0.0.1	www.flink.com	9300
      val split = x.split("\t")
      val level = split(2)
      val timeStr = split(3)
      // 时间转化成long 数字 2019-11-08 16:33:19
      var time = 0L
      try {
        val sourceFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
        time = sourceFormat.parse(timeStr).getTime
      } catch {
        case e: Exception => {
          //使用 logger
          logger.error(s"time parser error:$timeStr", e.getMessage)
        }
      }

      val domain = split(5)
      val traffic = split(6).toLong
      (level, time, domain, traffic)
    }).filter(x => x._2 != 0)
      .filter(x => x._1 == "E")
      .map(x => {
        (x._2, x._3, x._4)
      })

    // logData.print().setParallelism(1)
    // 使用水印 = logData 1573203524000,www.mysql.com,8897
    val esData = logData.assignTimestampsAndWatermarks(new AssignerWithPeriodicWatermarks[(Long, String, Long)] {
      //最大的一个无序的容忍时间 ，即等待时间
      val maxOutOfOrderness = 3500L // 3.5 seconds
      //当前最大的时间
      var currentMaxTimeStamp: Long = _

      //获取当前最大的时间
      override def getCurrentWatermark: Watermark = {
        //println(s"执行了...............getCurrentWatermark=$currentMaxTimeStamp")
        new Watermark(currentMaxTimeStamp - maxOutOfOrderness)
      }

      override def extractTimestamp(element: (Long, String, Long), previousElementTimestamp: Long): Long = {
        //println("执行了。。。。。。。。。。。。。。。。。。。extractTimestamp")
        val timestamp = element._1 // 获取当前数据产生的时间
        currentMaxTimeStamp = Math.max(timestamp, currentMaxTimeStamp)
        timestamp
      }
    }).keyBy(1)
      .timeWindow(Time.seconds(60))
      .apply(new WindowFunction[(Long, String, Long), (String, String, Long), Tuple, TimeWindow] {
        override def apply(key: Tuple, window: TimeWindow, input: Iterable[(Long, String, Long)], out: Collector[(String, String, Long)]): Unit = {
          /**
            * 第一个参数：这一分钟的时间
            * 第二个参数：域名
            * 第三个参数：trafficr的和
            */
          val domain = key.getField(0).toString
          var sum = 0L
          val iterator = input.iterator
          val times = ArrayBuffer[Long]()
          while (iterator.hasNext) {
            val next = iterator.next()
            sum += next._3
            times.append(next._1)
          }
          val time = new SimpleDateFormat("yyyy-MM-dd HH:mm").format(new Date(times.max))
          out.collect((time, domain, sum))
        }
      })

    val httpHosts = new util.ArrayList[HttpHost]()
    httpHosts.add(new HttpHost("192.168.226.128", 9200, "http"))
    val esSinkBuilder = new ElasticsearchSink.Builder[(String, String, Long)](
      httpHosts,

      new ElasticsearchSinkFunction[(String, String, Long)] {
        def createIndexRequest(element: (String, String, Long)): IndexRequest = {
          val json = new java.util.HashMap[String, Any]
          json.put("time", element._1)
          json.put("domain", element._2)
          json.put("traffic", element._3)
          val id = element._1 + "-" + element._2
          return Requests.indexRequest()
            .index("holiday")
            .`type`("traffic")
            .id(id)
            .source(json)
        }

        override def process(t: (String, String, Long), ctx: RuntimeContext, indexer: RequestIndexer) = {
          indexer.add(createIndexRequest(t))
        }
      }
    )
    esSinkBuilder.setBulkFlushMaxActions(1)
    esData.addSink(esSinkBuilder.build())


    env.execute(this.getClass.getName)
  }
}
