package DataStream.flink.connector

import java.util.Properties

import org.apache.flink.api.common.serialization.SimpleStringSchema
import org.apache.flink.streaming.api.scala.StreamExecutionEnvironment
import org.apache.flink.streaming.connectors.kafka.FlinkKafkaConsumer
import org.apache.flink.api.scala._
import org.apache.flink.streaming.api.CheckpointingMode

object KafkaConnectorConsumerApp {
  def main(args: Array[String]): Unit = {

    val env = StreamExecutionEnvironment.getExecutionEnvironment
    // 启用Checkpointing
    env.enableCheckpointing(4000) // 每隔40秒检查一次
    env.getCheckpointConfig.setCheckpointingMode(CheckpointingMode.EXACTLY_ONCE)
    env.getCheckpointConfig.setCheckpointTimeout(10000) // 设置超时时间
    env.getCheckpointConfig.setMaxConcurrentCheckpoints(1) // 同一时间只允许进行一个检查点

    // 设置kafka 读取主题
    val topic = "test"
    // 设置从kafka配置
    val pro = new Properties()
    pro.setProperty("bootstrap.servers", "192.168.226.128:9092")
    pro.setProperty("group.id", "test")
    // 创建consumer
    val consumer = new FlinkKafkaConsumer[String](topic, new SimpleStringSchema(), pro)
    val data = env.addSource(consumer)

    data.print()
    env.execute("KafkaConnectorConsumerApp")
  }
}
