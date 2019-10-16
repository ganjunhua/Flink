package com.holiday.flink.datasource

import org.apache.flink.api.scala.ExecutionEnvironment
import org.apache.flink.api.scala._
import org.apache.flink.configuration.Configuration

object DataSetDataSourceApp {
  def main(args: Array[String]): Unit = {
    val env = ExecutionEnvironment.getExecutionEnvironment
    //fromCollection(env)
    //textFile(env)
    readRecursiveFiles(env)
  }

  def readCompressionFile(env: ExecutionEnvironment): Unit = {
    env.readTextFile("data").print()
  }

  def readRecursiveFiles(env: ExecutionEnvironment): Unit = {
    // 创建递归配置
    val parameters = new Configuration()
    //设置递归属性
    parameters.setBoolean("recursive.file.enumeration", true)
    //使用递归配置 .withParameters(parameters)
    env.readTextFile("data").withParameters(parameters).print()
  }

  def csvFile(env: ExecutionEnvironment): Unit = {
    val dataPath = "data/holiday.csv"
    // 读取所有的列[(String, Int, String)] 定义数据类型,过虑表头 ignoreFirstLine = true
    //  env.readCsvFile[(String, Int, String)](dataPath, ignoreFirstLine = true).print()
    //只需读取1，3列
    //env.readCsvFile[(String, String)](dataPath, ignoreFirstLine = true, includedFields = Array(0, 2)).print()
    //使用类 获取列
    // case class Holiday(name: String, age: Int)
    //env.readCsvFile[Holiday](dataPath, ignoreFirstLine = true, includedFields = Array(0, 1)).print()
    //使用pojo  java 类
    env.readCsvFile[Person](dataPath, ignoreFirstLine = true, pojoFields = Array("name", "age", "work")).print()
  }

  def fromCollection(env: ExecutionEnvironment): Unit = {
    val data = 1 to 10
    env.fromCollection(data).print()
  }

  def textFile(env: ExecutionEnvironment): Unit = {
    val dataPath = "data/wc"
    // 读取本地，或hdfs 文件
    //env.readTextFile(dataPath).print()
    // 读取本地，或hdfs 文件夹
    env.readTextFile("data").print()
  }
}
