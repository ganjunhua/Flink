package com.holiday.flink.train.project

import java.text.SimpleDateFormat
import java.util.{Date, Properties}

import org.apache.flink.api.common.functions.RuntimeContext
import org.apache.flink.api.common.serialization.SimpleStringSchema
import org.apache.flink.api.java.tuple.Tuple
import org.apache.flink.api.scala._
import org.apache.flink.streaming.api.TimeCharacteristic
import org.apache.flink.streaming.api.functions.AssignerWithPunctuatedWatermarks
import org.apache.flink.streaming.api.functions.co.CoFlatMapFunction
import org.apache.flink.streaming.api.scala.StreamExecutionEnvironment
import org.apache.flink.streaming.api.scala.function.WindowFunction
import org.apache.flink.streaming.api.watermark.Watermark
import org.apache.flink.streaming.api.windowing.assigners.TumblingEventTimeWindows
import org.apache.flink.streaming.api.windowing.time.Time
import org.apache.flink.streaming.api.windowing.windows.TimeWindow
import org.apache.flink.streaming.connectors.elasticsearch.{ElasticsearchSinkFunction, RequestIndexer}
import org.apache.flink.streaming.connectors.elasticsearch6.ElasticsearchSink
import org.apache.flink.streaming.connectors.kafka.FlinkKafkaConsumer
import org.apache.flink.util.Collector
import org.apache.http.HttpHost
import org.elasticsearch.action.index.IndexRequest
import org.elasticsearch.client.Requests
import org.slf4j.LoggerFactory

import scala.collection.mutable
import scala.collection.mutable.ArrayBuffer

object LogAnalysis2 {
  // 在生产上记录日志建议采用这种方式
  def logger = LoggerFactory.getLogger("LogAnalysis")

  def main(args: Array[String]): Unit = {
    val env = StreamExecutionEnvironment.getExecutionEnvironment
    env.setStreamTimeCharacteristic(TimeCharacteristic.EventTime)
    env.setParallelism(3)
    val topic = "flink"
    val properties = new Properties()
    properties.setProperty("bootstrap.servers", "holiday-3:9092")
    properties.setProperty("group.id", "flink")
    val consumer = new FlinkKafkaConsumer[String](
      topic,
      new SimpleStringSchema(),
      properties)
    //获取kafka里面的数据
    val data = env.addSource(consumer).setParallelism(3)
    val logdata = data.map(x => {
      val splits = x.split("\t")
      val level = splits(2)
      val timeStr = splits(3)
      var time = 0L
      try {
        // 时间转换为Long
        val sourceFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
        time = sourceFormat.parse(timeStr).getTime
      } catch {
        case e: Exception => {
          logger.error(s"time parse error:$timeStr" + e.getMessage)
        }
      }

      val domain = splits(5)
      val traffic = splits(6).toLong
      (level, time, domain, traffic)
    }).filter(_._2 != 0)
      .filter(_._1 == "E")
      .map(x => {
        (x._2, x._3, x._4)
      })
    // 往es 里面写入数据
    //val httpHosts = new util.ArrayList[HttpHost]
    // httpHosts.add(new HttpHost("holiday-3", 9200, "http"))


    //加载mysql的数据
    val mysqlData = env.addSource(new HolidayMysql).setParallelism(1)
    //两个数据流连接起来，可以细化处理流里面的数据
    val connectionData = logdata.connect(mysqlData).flatMap(new CoFlatMapFunction[(Long, String, Long), mutable.HashMap[String, String], String] {
      var userDomainMap = mutable.HashMap[String, String]()

      // 这个是处理logdata
      override def flatMap1(value: (Long, String, Long), out: Collector[String]) = {
        val domain = value._2
        val userID = userDomainMap.getOrElse(domain, "")
        println("========" + value._1 + "\t" + value._2 + "\t" + userID)
      }

      //这个是处理mysql
      override def flatMap2(value: mutable.HashMap[String, String], out: Collector[String]) = {
        userDomainMap = value
      }

    })
    connectionData.print()

    env.execute(this.getClass.getSimpleName)
  }
}
