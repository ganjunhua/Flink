package com.holiday.flink.train.dataset.course04

import org.apache.flink.api.scala.ExecutionEnvironment
import org.apache.flink.api.scala._

object DataSetDataSourceApp {
  def main(args: Array[String]): Unit = {
    val env = ExecutionEnvironment.getExecutionEnvironment
    //fromCollection(env)
    textFile(env)
  }

  def textFile(env: ExecutionEnvironment): Unit = {
    // 读取指定文件
    var filePath = "data/hello"
    env.readTextFile(filePath).print()
    //读取指定文件夹
    filePath = "data"
    env.readTextFile(filePath).print()
  }

  def fromCollection(env: ExecutionEnvironment): Unit = {
    val data = 1 to 10
    env.fromCollection(data)
      .print()
  }

}
