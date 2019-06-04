package com.holiday.flink.train.dataset.course04

import org.apache.commons.io.FileUtils
import org.apache.flink.api.common.functions.RichMapFunction
import org.apache.flink.api.scala.ExecutionEnvironment
import org.apache.flink.api.scala._
import org.apache.flink.configuration.Configuration

import scala.collection.mutable.ListBuffer

object DataSetDistributedCacheApp {
  def main(args: Array[String]): Unit = {
    val env = ExecutionEnvironment.getExecutionEnvironment
    // step1:注册一个本地 /hdfs文件  或 文件夹
    val filePath = "data/hello"
    env.registerCachedFile(filePath, "holiday-scala")
    var list = ListBuffer[String]()
    val data = env.fromElements("hadoop", "spark", "flink")
    data.map(new RichMapFunction[String, String] {
      override def map(value: String) = {
        value
      }

      override def open(parameters: Configuration): Unit = {
        // step2：获取文件
        val dcFIle = getRuntimeContext.getDistributedCache().getFile("holiday-scala")
        //step3:获取文件内容
        val lines = FileUtils.readLines(dcFIle)

        /**
          * 此时会出现一个异常：java集合和scala集合不兼容的问题
          * 需要导入 scala.collection.JavaConverters._
          */
        import scala.collection.JavaConverters._
        for (ele <- lines.asScala) {
          list.append(ele)
          println(ele)

        }
      }
    }).print()
    //env.execute(this.getClass.getSimpleName)
    println("xxxxxxxxxx")
     println("list:"+list.size)
  }
}
