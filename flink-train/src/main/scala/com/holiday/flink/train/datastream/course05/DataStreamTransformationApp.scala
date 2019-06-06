package com.holiday.flink.train.datastream.course05

import java.{lang, util}

import org.apache.flink.streaming.api.scala.StreamExecutionEnvironment
import org.apache.flink.api.scala._
import org.apache.flink.streaming.api.collector.selector.OutputSelector

object DataStreamTransformationApp {
  def main(args: Array[String]): Unit = {
    val env = StreamExecutionEnvironment.getExecutionEnvironment
    //filterFunction(env)
    // unionFunction(env)
    splitSelectFunction(env)
    env.execute(this.getClass.getSimpleName)
  }

  def splitSelectFunction(env: StreamExecutionEnvironment): Unit = {
    val data = env.addSource(new CustomNonParallelSourceFunctionApp)
    data.split(new OutputSelector[Long] {
      override def select(value: Long): lang.Iterable[String] = {
        val list = new util.ArrayList[String]()
        if (value % 2 == 0) {
          list.add("even")
        } else {
          list.add("odd")
        }
        list
      }

      // select("even")  是通过split流中定义的
    }).select("even").print()
  }

  def filterFunction(env: StreamExecutionEnvironment): Unit = {
    val data = env.addSource(new CustomNonParallelSourceFunctionApp)
    data.map(x => {
      println("received: " + x)
      x
    }).filter(_ % 2 == 0).print()

  }

  def unionFunction(env: StreamExecutionEnvironment): Unit = {
    val data1 = env.addSource(new CustomNonParallelSourceFunctionApp)
    val data2 = env.addSource(new CustomNonParallelSourceFunctionApp)
    data1.union(data2).print()
  }

}
