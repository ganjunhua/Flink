package DataStream.flink.connector;

import org.apache.flink.api.common.serialization.SimpleStringSchema;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.connectors.kafka.FlinkKafkaConsumer;

import java.util.Properties;

public class JavaKafkaConnectorConsumerApp {
    public static void main(String[] args) throws Exception {
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        Properties pro = new Properties();
        pro.setProperty("bootstrap.servers", "192.168.226.128:9092");
        pro.setProperty("group.id", "test");
        String topic = "test";
        FlinkKafkaConsumer consumer = new FlinkKafkaConsumer(topic, new SimpleStringSchema(), pro);
        env.addSource(consumer).print();
        env.execute("JavaKafkaConnectorConsumerApp");
    }
}
