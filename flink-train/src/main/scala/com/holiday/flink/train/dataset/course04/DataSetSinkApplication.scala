package com.holiday.flink.train.dataset.course04

import org.apache.flink.api.scala.ExecutionEnvironment
import org.apache.flink.api.scala._
import org.apache.flink.core.fs.FileSystem
import org.apache.flink.core.fs.FileSystem.WriteMode

object DataSetSinkApplication {
  def main(args: Array[String]): Unit = {
    val env = ExecutionEnvironment.getExecutionEnvironment
    val data = 1.to(10)
    val text = env.fromCollection(data)
    val filePath = "data/sink/text.csv"
    //如果没有设置并行度，路径最后一层为文件，如果设置了 setParallelism(4) 则路径最后层为文件夹
    text.writeAsText(filePath, WriteMode.OVERWRITE)
    env.execute(this.getClass.getSimpleName)
  }
}
