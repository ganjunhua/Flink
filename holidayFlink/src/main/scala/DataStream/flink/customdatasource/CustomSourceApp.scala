package DataStream.flink.customdatasource

import org.apache.flink.streaming.api.scala.StreamExecutionEnvironment
import org.apache.flink.api.scala._
object CustomSourceApp {
  def main(args: Array[String]): Unit = {
    val env = StreamExecutionEnvironment.getExecutionEnvironment
    CustomParalleleSourceFunction(env)
    env.execute(this.getClass.getSimpleName)
  }

  def CustomParalleleSourceFunction(env: StreamExecutionEnvironment): Unit = {
    val data = env.addSource(new CustomParalleleSource).setParallelism(2)
    data.print()
  }
}
