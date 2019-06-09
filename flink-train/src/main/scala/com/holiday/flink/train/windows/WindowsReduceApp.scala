package com.holiday.flink.train.windows

import org.apache.flink.streaming.api.scala.StreamExecutionEnvironment
import org.apache.flink.streaming.api.windowing.time.Time
import org.apache.flink.api.scala._

object WindowsReduceApp {
  def main(args: Array[String]): Unit = {
    val env = StreamExecutionEnvironment.getExecutionEnvironment
    val text = env.socketTextStream("holiday-7-1", 9999)
    text.flatMap(_.split(",")).map(x => (1, x.toInt))
      .keyBy(0) // 因为key都 一样1，所以所有 的元素都到一个task去执行
      .timeWindow(Time.seconds(5))
      .reduce((v1, v2) => {
        //不是等待窗口拔腿的数据进行一次性处理，而是数据两两处理
        println(v1 + "==========" + v2)
        (v1._1, v1._2 + v2._2)
      })
      .print()
      .setParallelism(1)
    env.execute("WindowsReduceApp")

  }


}
