package com.holiday.flink.scala.course02

import org.apache.flink.streaming.api.scala.StreamExecutionEnvironment
import org.apache.flink.streaming.api.windowing.time.Time
import org.apache.flink.api.scala._

/**
  * 使用scala开发flink的实时处理应用程序
  */
object StreamingWCScalaApp {
  def main(args: Array[String]): Unit = {
    val env = StreamExecutionEnvironment.getExecutionEnvironment
    val text = env.socketTextStream("holiday-7-1", 9999)
    text.flatMap(x => x.split(" "))
      .map(x => (x, 1))
      .keyBy(0)
      .timeWindow(Time.seconds(5)).sum(1)
      .print()
      .setParallelism(1)
    // step4:流式处理一定要加此代码
    env.execute("Flink Streaming Scala API Skeleton")
  }
}
