package DataStream.flink.window

import org.apache.flink.api.scala._
import org.apache.flink.streaming.api.scala.StreamExecutionEnvironment
import org.apache.flink.streaming.api.windowing.time.Time

object WindowsReduceApp {
  def main(args: Array[String]): Unit = {
    val env = StreamExecutionEnvironment.getExecutionEnvironment
    val text = env.socketTextStream("192.168.226.128", 9999)
    // 原来传递进来的数据是字符串，此处我们就使用数值类型，通过数值类型演示增量效果
    text.flatMap(_.split(","))
      .map(x => (1, x.toInt)) // 1 ,2,3,4,=>(1,1) (1,2),(1,3),(1,4)
      .keyBy(0) //因为key都1，所以所有的元素都到一个task执行
      .timeWindow(Time.seconds(5))

      .reduce((x, y ) => {// 不是等窗口所有的数据进行一次性处理，再是数据两两处理
        println(x + ",,," + y)
        (x._1, x._2 + y._2)
      })
      .print()
      .setParallelism(1)

    env.execute("xx")
  }

}
