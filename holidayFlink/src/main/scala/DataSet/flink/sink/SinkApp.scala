package DataSet.flink.sink

import org.apache.flink.api.scala.ExecutionEnvironment
import org.apache.flink.api.scala._
import org.apache.flink.core.fs.FileSystem.WriteMode

object SinkApp {
  def main(args: Array[String]): Unit = {

    val env = ExecutionEnvironment.getExecutionEnvironment
    val data = 1 to 10
    val text = env.fromCollection(data)
    val filePath = "data/sink"
    text.writeAsText(filePath,WriteMode.OVERWRITE).setParallelism(2)
    env.execute(this.getClass.getSimpleName)
  }
}
