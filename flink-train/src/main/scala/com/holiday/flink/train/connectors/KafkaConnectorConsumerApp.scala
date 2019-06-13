package com.holiday.flink.train.connectors

import java.util.Properties

import org.apache.flink.api.common.serialization.SimpleStringSchema
import org.apache.flink.streaming.api.scala.StreamExecutionEnvironment
import org.apache.flink.streaming.connectors.kafka.FlinkKafkaConsumer
import org.apache.flink.api.scala._
import org.apache.flink.streaming.api.CheckpointingMode

/**
  * flink 结合kafka 消费者
  */
object KafkaConnectorConsumerApp {
  def main(args: Array[String]): Unit = {
    // 创建执行环境
    val env = StreamExecutionEnvironment.getExecutionEnvironment

    // 常用配置 checkpoint
    // 每隔多少 checkpointing一次，单位毫秒 启用检查点
    env.enableCheckpointing(4000)
    //checkpoint  精确执行一次
    env.getCheckpointConfig.setCheckpointingMode(CheckpointingMode.EXACTLY_ONCE)
    //如果在此之前未完成，则中止正在执行的检查点的时间。 检查点必须在一分钟内完成，或者被丢弃

    env.getCheckpointConfig.setCheckpointTimeout(60000)
    //默认情况下，当一个检查点仍在运行时，系统不会触发另一个检查点。这确保拓扑不会在检查点上花费太多时间，也不会在处理流方面取得进展。可以允许多个重叠的检查点，这对于具有一定处理延迟(例如，因为函数调用需要一些时间来响应的外部服务)但仍然希望执行非常频繁的检查点(100毫秒)以在出现故障时很少重新处理的管道来说非常有趣。
    //当定义检查点之间的最小时间时，不能使用此选项。
    env.getCheckpointConfig.setMaxConcurrentCheckpoints(1)
    //定义topic
    val topic = "flink"
    //定义properties
    val properties = new Properties()
    //设置 properties 的 zk  与消费组
    properties.setProperty("bootstrap.servers", "holiday-3:9092")
    properties.setProperty("group.id", "testFlink")
    //创建 kafka消费者
    //设置数据来源，FlinkKafkaConsumer [string kafka里面的数据类型]
    val kafkaConsumer = new FlinkKafkaConsumer[String](topic, new SimpleStringSchema(), properties)
    //设置kafka的offset
    kafkaConsumer.setStartFromLatest()
    val data = env.addSource(kafkaConsumer)
    data.print()

    env.execute("KafkaConnectorConsumerApp")
  }
}
