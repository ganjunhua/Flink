package com.holiday.flink.train.dataset.course04

import org.apache.flink.api.common.accumulators.LongCounter
import org.apache.flink.api.common.functions.RichMapFunction
import org.apache.flink.api.scala.ExecutionEnvironment
import org.apache.flink.api.scala._
import org.apache.flink.configuration.Configuration
import org.apache.flink.core.fs.FileSystem.WriteMode

object DataSetCounterApp {
  def main(args: Array[String]): Unit = {
    val env = ExecutionEnvironment.getExecutionEnvironment
    // 从元素中创建数据 dataset
    val data = env.fromElements("holiday", "hadoop", "spark", "flink", "scala", "oracle")

    /*//  计数器，如果增加并行度  setParallelism(4) 则计算出来的结果是不对的
    var counter = 0l
    data.map(x => {
      counter += 1
      println("map : " + counter)
      counter
    }).setParallelism(4).print()*/
    // data.print()

    val counterName="DataSetCounterApp"
    val info = data.map(new RichMapFunction[String, String]() {
      // step1:定义计算器
      var counter = new LongCounter()

      override def open(parameters: Configuration): Unit = {
        //step2:注册计数器,参数1：计数器名，自定义，计数器类
        getRuntimeContext.addAccumulator(counterName, counter)
      }
      override def map(value: String): String = {
        // step3计数器 累加数字
        counter.add(1)
        value
      }
    })
    info.setParallelism(4)
    info.writeAsText("data/sink/counter", WriteMode.OVERWRITE)
    //step4:根据计数名，获取计数结果
    val jobReuslt = env.execute("DataSetCounterApp")
    val resultNum = jobReuslt.getAccumulatorResult[Long](counterName)
    //step5:打印计数结果
    println(resultNum)
  }
}
