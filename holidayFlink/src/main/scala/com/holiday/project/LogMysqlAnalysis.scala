package com.holiday.project

import java.text.SimpleDateFormat
import java.util
import java.util.{Date, Properties}

import org.apache.flink.api.common.functions.RuntimeContext
import org.apache.flink.api.common.serialization.SimpleStringSchema
import org.apache.flink.api.java.tuple.Tuple
import org.apache.flink.api.scala._
import org.apache.flink.streaming.api.TimeCharacteristic
import org.apache.flink.streaming.api.functions.AssignerWithPeriodicWatermarks
import org.apache.flink.streaming.api.functions.co.CoFlatMapFunction
import org.apache.flink.streaming.api.scala.StreamExecutionEnvironment
import org.apache.flink.streaming.api.scala.function.WindowFunction
import org.apache.flink.streaming.api.watermark.Watermark
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

object LogMysqlAnalysis {
  //在生产中记录日志建议采用这种方式

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
    //logData.print()

    //读取Mysql
    val mysqlData = env.addSource(new MysqlSource)
    // mysqlData.print()
    //两个数据连接
    val connectData = logData.connect(mysqlData)
      .flatMap(new CoFlatMapFunction[(Long, String, Long), mutable.HashMap[String, String], String] {
        var userDomainMap = mutable.HashMap[String, String]()

        //这个是处理logdata的数据集
        override def flatMap1(value: (Long, String, Long), out: Collector[String]) = {
          val domain = value._2
          //根据domain 从mysql数据集提取userid
          val userid = userDomainMap.getOrElse(domain, "")

          out.collect(value._1 + "---" + value._2 + "---" + value._3 + "---" + userid)
        }

        // 这个是处理mysql的数据集
        override def flatMap2(value: mutable.HashMap[String, String], out: Collector[String]) = {
          userDomainMap = value
        }
      })
    connectData.print()
    env.execute("asdfa")
  }
}
