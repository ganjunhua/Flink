package com.holiday.flink.fieldsKey

import org.apache.flink.api.scala._
import org.apache.flink.streaming.api.scala.StreamExecutionEnvironment
import org.apache.flink.streaming.api.windowing.time.Time

object StreamingWCScalaApp {

  //定义class，即定义表字段
  case class WC(word: String, count: Int)

  def main(args: Array[String]): Unit = {
    val env = StreamExecutionEnvironment.getExecutionEnvironment

    val text = env.socketTextStream("192.168.226.128", 9999)
    val words = text.flatMap(_.split(","))
      //将结果放入类中
      .map(WC(_, 1))
      // 使用字段进行分组
      .keyBy("word")
      .timeWindow(Time.seconds(5))
      // 使用字段进行分组
      .sum("count")
    words.print()
    env.execute()
  }
}
