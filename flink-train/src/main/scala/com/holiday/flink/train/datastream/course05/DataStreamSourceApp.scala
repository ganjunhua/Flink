package com.holiday.flink.train.datastream.course05

import org.apache.flink.streaming.api.scala.{DataStream, StreamExecutionEnvironment}
import org.apache.flink.api.scala._

object DataStreamSourceApp {
  def main(args: Array[String]): Unit = {
    // step1:获取执行环境
    val env = StreamExecutionEnvironment.getExecutionEnvironment;
    //step2:执行transfomation
    //socketFunction(env)
    //nonParallelSourceFunction(env)
    //parallelSourceFunction(env)
    richParallelSourceFunction(env)
    //step3:运行 stream
    env.execute(this.getClass.getSimpleName)
  }
  def richParallelSourceFunction(env: StreamExecutionEnvironment): Unit = {
    val data = env.addSource(new CustomRichParallelSourceFunction).setParallelism(2)
    data.print()
  }
  def parallelSourceFunction(env: StreamExecutionEnvironment): Unit = {
    val data = env.addSource(new CustomParallelSourceFunction).setParallelism(2)
    data.print()
  }

  def nonParallelSourceFunction(env: StreamExecutionEnvironment): Unit = {
    val data = env.addSource(new CustomNonParallelSourceFunctionApp)
    data.print()
  }

  def socketFunction(env: StreamExecutionEnvironment): Unit = {
    val data: DataStream[String] = env.socketTextStream("holiday-1", 9999)
    data.print().setParallelism(3)
  }
}
