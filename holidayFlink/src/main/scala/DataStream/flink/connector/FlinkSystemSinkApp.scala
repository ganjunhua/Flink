package DataStream.flink.connector

import org.apache.flink.streaming.api.scala.StreamExecutionEnvironment
import org.apache.flink.streaming.connectors.fs.StringWriter
import org.apache.flink.streaming.connectors.fs.bucketing.{BucketingSink, DateTimeBucketer}

object FlinkSystemSinkApp {
  def main(args: Array[String]): Unit = {
    val env = StreamExecutionEnvironment.getExecutionEnvironment
    val data = env.socketTextStream("192.168.226.128", 9999)
    val filePath = "data/connector"
    val sink1 = new BucketingSink[String](filePath)
    //设置文件名
    sink1.setBucketer(new DateTimeBucketer[String]("yyyy-MM-dd--HHmm"))
    //设置写入方式是 utf-8
    sink1.setWriter(new StringWriter[String]())
    //设置每次批次大写
    sink1.setBatchSize(1020 * 1024 * 400)
    //设置每次滚动时间 20 秒
    sink1.setBatchRolloverInterval(2000)
    // 将数据添加至sink
    data.addSink(sink1)
    env.execute("FlinkSystemSinkApp")
  }
}
