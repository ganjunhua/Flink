package DataStream.flink.datastream

import java.{lang, util}

import DataStream.flink.customdatasource.CustomNonParalleleSource
import org.apache.flink.streaming.api.scala.StreamExecutionEnvironment
import org.apache.flink.api.scala._
import org.apache.flink.streaming.api.collector.selector.OutputSelector

object DataStreamTransformationApp {
  def main(args: Array[String]): Unit = {
    val env = StreamExecutionEnvironment.getExecutionEnvironment
    filterFunction(env)
    env.execute("xx")

  }



  def filterFunction(env: StreamExecutionEnvironment): Unit = {
    val data = env.addSource(new CustomNonParalleleSource)
    data.map(x => {
      println("xxx" + x)
      x
    }).filter(_ % 2 == 0).print()
  }
}
