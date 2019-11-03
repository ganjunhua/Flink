package DataStream.flink.connector

import java.util.Properties

import org.apache.flink.api.common.serialization.SimpleStringSchema
import org.apache.flink.streaming.api.scala.StreamExecutionEnvironment
import org.apache.flink.streaming.connectors.kafka.FlinkKafkaProducer
import org.apache.flink.streaming.util.serialization.{KeyedSerializationSchema, KeyedSerializationSchemaWrapper}


object KafkaConnectorSinkApp {
  def main(args: Array[String]): Unit = {
    val env = StreamExecutionEnvironment.getExecutionEnvironment

    //从socket接收数据，通过flink，将数据Sink到kafka, 充当生产者
    val data = env.socketTextStream("192.168.226.128", 9999)
    // Sink kafka
    val topic = "test"
    val pro = new Properties()
    pro.setProperty("bootstrap.servers", "192.168.226.128:9092")
    //方法1：创建kafka sink
    //val kafkaSink1 = new FlinkKafkaProducer[String](topic, new KeyedSerializationSchemaWrapper[String](new SimpleStringSchema()), pro)
    //方法2：创建kafka sink
   // val kafkaSink = new FlinkKafkaProducer[String]("192.168.226.128:9092", topic, new SimpleStringSchema())

    val kafkaSink1 = new FlinkKafkaProducer[String](topic,
      new KeyedSerializationSchemaWrapper[String](new SimpleStringSchema()),
      pro,
      FlinkKafkaProducer.Semantic.EXACTLY_ONCE) // ExAcTLY_ONCE语义 容错 可以提供至少一次的交付保证


    data.addSink(kafkaSink1)
    env.execute("KafkaConnectorSinkApp")
  }
}
