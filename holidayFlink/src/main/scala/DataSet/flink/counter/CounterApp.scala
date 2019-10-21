package DataSet.flink.counter

import org.apache.flink.api.common.accumulators.LongCounter
import org.apache.flink.api.common.functions.RichMapFunction
import org.apache.flink.api.scala.ExecutionEnvironment
import org.apache.flink.api.scala._
import org.apache.flink.configuration.Configuration
import org.apache.flink.core.fs.FileSystem.WriteMode

object CounterApp {
  def main(args: Array[String]): Unit = {
    val env = ExecutionEnvironment.getExecutionEnvironment
    val data = env.fromElements("hadoop", "spark", "flink")
    val info = data.map(new RichMapFunction[String, String]() {
      // step:1定义计数器
      val counter = new LongCounter()

      override def open(parameters: Configuration): Unit = {
        //step2:注册计算器
        getRuntimeContext.addAccumulator("xx", counter)
        println("开始执行open方法......")
      }

      override def map(value: String) = {
        //step3:累加计算
        counter.add(1)
        println("开始执行map方法......")
        value
      }
    })
    info.writeAsText("data/counter",WriteMode.OVERWRITE).setParallelism(3)
    val result = env.execute("a")
    val info1 = result.getAccumulatorResult[Long]("xx")
    println(info1)
  }
}
