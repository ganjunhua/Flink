package DataStream.flink.Socket;

import org.apache.flink.streaming.api.datastream.DataStreamSource;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;

public class JavaDataStreamSourceApp {
    public static void main(String[] args) throws Exception {
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        socketFunction(env);
        env.execute("JavaDataStreamSourceApp");
    }

    public static void socketFunction(StreamExecutionEnvironment env) {
        DataStreamSource<String> data = env.socketTextStream("192.168.226.128", 9999);
        data.print().setParallelism(1);
    }
}
