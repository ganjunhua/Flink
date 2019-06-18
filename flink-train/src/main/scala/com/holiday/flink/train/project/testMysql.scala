package com.holiday.flink.train.project

import org.apache.flink.streaming.api.scala.StreamExecutionEnvironment
import org.apache.flink.api.scala._

object testMysql {
  def main(args: Array[String]): Unit = {
    val env = StreamExecutionEnvironment.getExecutionEnvironment
    val data = env.addSource(new HolidayMysql).setParallelism(1)
    data.print()

    env.execute("testMysql")
  }
}
