package com.holiday.flink.train.project

import java.text.SimpleDateFormat
import java.util.{Date, Properties}

import org.apache.flink.api.common.serialization.SimpleStringSchema
import org.apache.flink.api.java.tuple.Tuple
import org.apache.flink.streaming.api.scala.StreamExecutionEnvironment
import org.apache.flink.streaming.connectors.kafka.FlinkKafkaConsumer
import org.apache.flink.api.scala._
import org.apache.flink.streaming.api.TimeCharacteristic
import org.apache.flink.streaming.api.functions.AssignerWithPunctuatedWatermarks
import org.apache.flink.streaming.api.scala.function.WindowFunction
import org.apache.flink.streaming.api.watermark.Watermark
import org.apache.flink.streaming.api.windowing.assigners.TumblingEventTimeWindows
import org.apache.flink.streaming.api.windowing.time.Time
import org.apache.flink.streaming.api.windowing.windows.TimeWindow
import org.apache.flink.util.Collector
import org.slf4j.LoggerFactory

import scala.collection.mutable.ArrayBuffer

object LogAnalysis {
  // 在生产上记录日志建议采用这种方式
  def logger = LoggerFactory.getLogger("LogAnalysis")

  def main(args: Array[String]): Unit = {
    val env = StreamExecutionEnvironment.getExecutionEnvironment
    env.setStreamTimeCharacteristic(TimeCharacteristic.EventTime)
    val topic = "flink"
    val properties = new Properties()
    properties.setProperty("bootstrap.servers", "holiday-3:9092")
    properties.setProperty("group.id", "flink")
    val consumer = new FlinkKafkaConsumer[String](
      topic,
      new SimpleStringSchema(),
      properties)
    //获取kafka里面的数据
    val data = env.addSource(consumer)
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
    // 处理乱序的问题，通过 watermark
    logdata.assignTimestampsAndWatermarks(new AssignerWithPunctuatedWatermarks[(Long, String, Long)]() {
      val maxOutOfOrderness = 10000L // 3.5 seconds

      var currentMaxTimestamp: Long = _ // _ 属于点位符

      override def checkAndGetNextWatermark(lastElement: (Long, String, Long), extractedTimestamp: Long) = {
        new Watermark(currentMaxTimestamp - maxOutOfOrderness)

      }

      override def extractTimestamp(element: (Long, String, Long), previousElementTimestamp: Long) = {
        val timestamp = element._1
        currentMaxTimestamp = Math.max(timestamp, currentMaxTimestamp)
        timestamp

      }
    }).keyBy(1) // 此处是按照域名进行keyby的
      .window(TumblingEventTimeWindows.of(Time.seconds(60)))
      .apply(new WindowFunction[(Long, String, Long), (String, String, Long), Tuple, TimeWindow] {
        override def apply(key: Tuple, window: TimeWindow, input: Iterable[(Long, String, Long)], out: Collector[(String, String, Long)]): Unit = {
          val domain = key.getField(0).toString
          var sum = 0L
          val iterator = input.iterator
          var times = ArrayBuffer[Long]()
          while (iterator.hasNext) {
            val next = iterator.next()
            sum += next._3
            times.append(next._1)
          }
          val time = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
            .format(new Date(times.max))
          out.collect(time, domain, sum)
        }
      }).print()
    env.execute(this.getClass.getSimpleName)
  }
}
