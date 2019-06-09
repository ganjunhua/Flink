package com.holiday.flink.train.windows

import org.apache.flink.streaming.api.scala.StreamExecutionEnvironment
import org.apache.flink.streaming.api.windowing.time.Time
import org.apache.flink.api.scala._

object WindowsApp {
  /**
    * 滚动窗口
    *
    * @param args
    */
  def main(args: Array[String]): Unit = {
    val env = StreamExecutionEnvironment.getExecutionEnvironment
    val text = env.socketTextStream("holiday-7-1", 9999)
    text.flatMap(_.split(",")).map((_, 1)).keyBy(0)
      // .timeWindow(Time.seconds(5))
      //.timeWindow(Time.seconds(2), Time.seconds(1))
      .sum(1)
      .print()
      .setParallelism(1)
    env.execute("WindowsApp")
  }

}
