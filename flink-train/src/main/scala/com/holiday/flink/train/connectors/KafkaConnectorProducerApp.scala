package com.holiday.flink.train.connectors

import java.util.Properties

import org.apache.flink.api.common.serialization.SimpleStringSchema
import org.apache.flink.streaming.api.scala.StreamExecutionEnvironment
import org.apache.flink.streaming.connectors.kafka.FlinkKafkaProducer
import org.apache.flink.streaming.connectors.kafka.internals.KeyedSerializationSchemaWrapper

/**
  * kafka 生产者 从socket接收数据，通过flink 将数据sink到kafka
  */
object KafkaConnectorProducerApp {
  def main(args: Array[String]): Unit = {
    val env = StreamExecutionEnvironment.getExecutionEnvironment
    //从 socket接收数据
    val data = env.socketTextStream("holiday-1", 9999)
    val topic = "flink"
    val properties = new Properties()
    properties.setProperty("bootstrap.servers", "holiday-3:9092")
    //创建 kafkaSink
    val kafkaSink = new FlinkKafkaProducer[String](topic,
      new KeyedSerializationSchemaWrapper[String](new SimpleStringSchema()), properties)
    //将数据发到 kafka里面
    data.addSink(kafkaSink)
    env.execute("KafkaConnectorProducerApp")
  }
}
