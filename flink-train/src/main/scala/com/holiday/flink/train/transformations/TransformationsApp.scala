package com.holiday.flink.train.transformations

import org.apache.flink.api.common.functions.ReduceFunction
import org.apache.flink.streaming.api.scala.{DataStream, StreamExecutionEnvironment}
import org.apache.flink.api.scala._
import org.apache.flink.streaming.api.windowing.time.Time

import scala.collection.mutable.ListBuffer

object TransformationsApp {
  def main(args: Array[String]): Unit = {
    val env = StreamExecutionEnvironment.getExecutionEnvironment
    val data = Array(1, 2, 3)
    newFlatMap(env, data)
    env.execute(this.getClass.getSimpleName)
  }

  def newMap(env: StreamExecutionEnvironment, data: Array[Int]): Unit = {
    val data1 = env.fromCollection(data)
    data1.map(_ + 1).print()
  }

  def newFlatMap(env: StreamExecutionEnvironment, data: Array[Int]): Unit = {
    val info = ListBuffer[String]()
    info.append("1,2")
    info.append("1,3")
    // info.append("2,8")
    // info.append("3,7")
    val data1 = env.fromCollection(info)
    val a = data1.flatMap(_.split(",")).map((_, 1))
    val c = a.keyBy(0)
    c.sum(1).print()
  }

}
