package DataSet.flink.distributed

import org.apache.commons.io.FileUtils
import org.apache.flink.api.common.functions.RichMapFunction
import org.apache.flink.api.scala.ExecutionEnvironment
import org.apache.flink.api.scala._
import org.apache.flink.configuration.Configuration

object DistributedCacheApp {
  def main(args: Array[String]): Unit = {
    val env = ExecutionEnvironment.getExecutionEnvironment
    val filePath = "data/wc"
    //step1:注册一个本地/hdfs文件
    env.registerCachedFile(filePath, "holiday")

    val data = env.fromElements("holiday", "flink")

    data.map(new RichMapFunction[String, String] {
      //step2:在open方法中获取注册的文件 "holiday"
      override def open(parameters: Configuration): Unit = {
        val hfile = getRuntimeContext.getDistributedCache.getFile("holiday")
        //step3:读取file内容
        import scala.collection.JavaConverters._
        val list = FileUtils.readLines(hfile)
        for (line <- list.asScala) {
          println(line)
        }
      }

      override def map(value: String) = {
        value
      }
    }).print()
  }
}
