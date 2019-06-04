package com.holiday.flink.train.dataset.course04

import org.apache.flink.api.common.functions.RichMapFunction
import org.apache.flink.api.scala.ExecutionEnvironment
import org.apache.flink.configuration.Configuration
import org.apache.flink.api.scala._
import scala.collection.JavaConverters._
import scala.collection.mutable.ListBuffer

object DataSetBroadcastApp {
  def main(args: Array[String]): Unit = {
    val env = ExecutionEnvironment.getExecutionEnvironment
    // step 1:声明广播变量数据
    val toBroadcast = env.fromElements("hadoop", "spark", "flink")
    val data = env.fromElements("xx", "yy")
    val info = data.map(new RichMapFunction[String, String]() {
      // 声明 广播变量返回
      var broadcastSet: Traversable[String] = null

      override def open(parameters: Configuration): Unit = {
        //step3:得到广播变量
        broadcastSet = getRuntimeContext.getBroadcastVariable[String]("Broadcast").asScala
      }

      def map(value: String) = {
        // 获取广播变量的值
        for (xx <- broadcastSet) {
          println("broadcastSet  ：" + xx)
        }
        // value 实际 data ("xx", "yy")   值
        println("value  :" + value)
        value
      }
      //step2:注册广播变量
    }).withBroadcastSet(toBroadcast, "Broadcast")

    info.setParallelism(4).print()
    // val result = env.execute(this.getClass.getSimpleName)

  }
}
