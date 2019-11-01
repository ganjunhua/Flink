package DataStream.flink.time

import org.apache.flink.streaming.api.TimeCharacteristic
import org.apache.flink.streaming.api.scala.StreamExecutionEnvironment

object Test {
  def main(args: Array[String]): Unit = {
    val env = StreamExecutionEnvironment.getExecutionEnvironment
    //设置FLink使用时间类型处理数据
    env.setStreamTimeCharacteristic(TimeCharacteristic.EventTime)
    env.execute("Test")
  }
}
