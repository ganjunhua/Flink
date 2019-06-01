package com.holiday.flink.scala.course03

import org.apache.flink.api.scala._
import org.apache.flink.streaming.api.scala.StreamExecutionEnvironment
import org.apache.flink.streaming.api.windowing.time.Time

/**
  * 使用scala开发flink的实时处理应用程序
  */
object StreamingWCScalaApp {

  case class WC(word: String, count: Int)

  def main(args: Array[String]): Unit = {
    val env = StreamExecutionEnvironment.getExecutionEnvironment
    val text = env.socketTextStream("holiday-7-1", 9999)
    text.flatMap(x => x.split(" "))
      .map(x => WC(x, 1))
      .keyBy("word")
      .timeWindow(Time.seconds(5)).sum("count")
      .print()
      .setParallelism(1)
    // step4:流式处理一定要加此代码
    env.execute("Flink Streaming Scala API Skeleton")
  }
}
