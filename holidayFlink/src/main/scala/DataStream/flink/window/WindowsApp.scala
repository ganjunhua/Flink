package DataStream.flink.window

import org.apache.flink.streaming.api.scala.StreamExecutionEnvironment
import org.apache.flink.streaming.api.windowing.time.Time
import org.apache.flink.api.scala._
object WindowsApp {
  def main(args: Array[String]): Unit = {
    val env = StreamExecutionEnvironment.getExecutionEnvironment
    val text = env.socketTextStream("192.168.226.128", 9999)
    text.flatMap(_.split(","))
      .map((_, 1))
      .keyBy(0)
     // .timeWindow(Time.seconds(5)) 滚动窗口
        .timeWindow(Time.seconds(10),Time.seconds(5)) //滑动窗口，两个参数，第一个是窗口大小，第二个是滑动的大小
      .sum(1)
      .print()
      .setParallelism(1)

    env.execute("xx")
  }

}
