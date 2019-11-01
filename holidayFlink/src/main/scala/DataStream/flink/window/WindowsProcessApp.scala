package DataStream.flink.window

import org.apache.flink.api.java.tuple.Tuple
import org.apache.flink.api.scala._
import org.apache.flink.streaming.api.scala.StreamExecutionEnvironment
import org.apache.flink.streaming.api.scala.function.ProcessWindowFunction
import org.apache.flink.streaming.api.windowing.time.Time
import org.apache.flink.streaming.api.windowing.windows.TimeWindow
import org.apache.flink.util.Collector

object WindowsReduceApp {
  def main(args: Array[String]): Unit = {
    val env = StreamExecutionEnvironment.getExecutionEnvironment
    val text = env.socketTextStream("192.168.226.128", 9999)

    text.flatMap(_.split(","))
      .map(x => (1, x.toInt)) // 1 ,2,3,4,=>(1,1) (1,2),(1,3),(1,4)
      .keyBy(_._1)
      .timeWindow(Time.minutes(5))


    env.execute("xx")
  }

}

