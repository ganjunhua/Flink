package com.holiday.flink.train.connectors

import org.apache.flink.streaming.api.scala.StreamExecutionEnvironment
import org.apache.flink.streaming.connectors.fs.StringWriter
import org.apache.flink.streaming.connectors.fs.bucketing.{BucketingSink, DateTimeBucketer}

/**
  * connector for file system
  */
object FileSystemSinkApp {
  def main(args: Array[String]): Unit = {
    //创建执行环境
    val env = StreamExecutionEnvironment.getExecutionEnvironment
    //接收数据
    val data = env.socketTextStream("holiday-1", 9999)
    //定义数据目标路径
    val filePath = "data/connectors/fileSink"
    val sink = new BucketingSink[String](filePath)
    //设置文件夹名称
    sink.setBucketer(new DateTimeBucketer[String]("yyyy-MM-dd--HHmm"))
    //设置写入方式是 utf-8
    sink.setWriter(new StringWriter[String]())
    //设置滚动时间
    sink.setBatchRolloverInterval(20)
    // 将数据添加至sink
    data.addSink(sink).setParallelism(1)
    env.execute("FileSystemSinkApp")
  }
}
