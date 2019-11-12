package com.holiday.project

import org.apache.flink.streaming.api.scala.StreamExecutionEnvironment
import org.apache.flink.api.scala._

object test {
  def main(args: Array[String]): Unit = {

    val env = StreamExecutionEnvironment.getExecutionEnvironment
    val data = env.addSource(new MysqlSource()).setParallelism(1)
    data.print().setParallelism(1)

    env.execute("xx")
  }
}
