package DataStream.flink.connector;

import org.apache.flink.api.common.serialization.SimpleStringSchema;
import org.apache.flink.streaming.api.datastream.DataStreamSource;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.connectors.kafka.FlinkKafkaProducer;

public class JavaKafkaConnectorSinkApp {
    public static void main(String[] args) throws Exception {
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        DataStreamSource<String> source = env.socketTextStream("192.168.226.128", 9999);
        FlinkKafkaProducer<String> producer = new FlinkKafkaProducer<String>("192.168.226.128:9092", "test", new SimpleStringSchema());
        source.addSink(producer);

        env.execute("JavaKafkaConnectorSinkApp");
    }
}
