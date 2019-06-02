package com.holiday.flink.train.dataset.course04

import org.apache.flink.api.scala.ExecutionEnvironment
import org.apache.flink.api.scala._

import scala.collection.mutable.ListBuffer

object DataSetTransformationApp {
  def main(args: Array[String]): Unit = {
    val env = ExecutionEnvironment.getExecutionEnvironment
    mapPartitionFunction(env)
  }

  def mapPartitionFunction(env: ExecutionEnvironment): Unit = {
    //dataSource 100个元素，把结果存储 到数据库中
    val students = new ListBuffer[String]
    for (i <- 1 to 100) {
      students.append("student: " + i)
    }
    val data = env.fromCollection(students).setParallelism(4)
    //mapPartition 每次处理一个分区 的数据，有多少分区，就会处理多少次
    // x.map 这个是处理每个分区具体里面的值
    data.mapPartition(x => x.map(x => {
      (x + "xz")
    })).print()
  }

  def filterFunction(env: ExecutionEnvironment): Unit = {
    val data = env.fromCollection(List(1 to 10: _*))
    data.map(_ + 1).filter(_ > 3).print()
  }

  def mapFunction(env: ExecutionEnvironment): Unit = {
    val data = env.fromCollection(List(1 to 10: _*))
    data.map(_.toInt).print()
  }
}
