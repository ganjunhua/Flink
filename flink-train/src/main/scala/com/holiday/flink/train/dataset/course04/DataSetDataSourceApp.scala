package com.holiday.flink.train.dataset.course04

import org.apache.flink.api.scala.ExecutionEnvironment
import org.apache.flink.api.scala._
import org.apache.flink.configuration.Configuration

object DataSetDataSourceApp {

  case class MyCaseClass(name: String, age: Int)

  def main(args: Array[String]): Unit = {
    val env = ExecutionEnvironment.getExecutionEnvironment
    //fromCollection(env)
    //textFile(env)
    readcompressionFile(env)
  }

  def csvFile(env: ExecutionEnvironment): Unit = {
    //读取 csv文件所有列
    val filePath = "data/people.csv"
    env.readCsvFile[(String, Int, String)](filePath, ignoreFirstLine = true).print()
    //读取csv文件指定列 includedFields = Array(0,1) 这个参数指定列，[(String, Int)] 这个指定列的类型
    env.readCsvFile[(String, Int)](filePath, ignoreFirstLine = true, includedFields = Array(0, 1)).print()
    // case class方式
    env.readCsvFile[MyCaseClass](filePath, ignoreFirstLine = true, includedFields = Array(0, 1)).print()
    //pojo
    env.readCsvFile[People](filePath, ignoreFirstLine = true, pojoFields = Array("name", "age", "work")).print()
  }

  def textFile(env: ExecutionEnvironment): Unit = {
    // 读取指定文件
    var filePath = "data/hello"
    env.readTextFile(filePath).print()
    //读取指定文件夹
    filePath = "data"
    env.readTextFile(filePath).print()
  }

  def readRecursiveFiles(env: ExecutionEnvironment): Unit = {
    // 递归读取文件夹 文件
    var filePath = "data"
    // 创建递归配置 对象
    val paraments = new Configuration()
    // 设置递归属性为 true
    paraments.setBoolean("recursive.file.enumeration", true)
    env.readTextFile(filePath).withParameters(paraments).print()
  }

  def  readcompressionFile(env:ExecutionEnvironment): Unit ={
    val filePath ="data/hello.rar"
    env.readTextFile(filePath).print()
  }
  def fromCollection(env: ExecutionEnvironment): Unit = {
    val data = 1 to 10
    env.fromCollection(data)
      .print()
  }

}
