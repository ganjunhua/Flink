package DataStream.flink.windows;

import org.apache.flink.api.common.functions.FlatMapFunction;
import org.apache.flink.api.common.functions.ReduceFunction;
import org.apache.flink.api.java.tuple.Tuple2;
import org.apache.flink.streaming.api.datastream.DataStreamSource;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.windowing.time.Time;
import org.apache.flink.util.Collector;

public class JavaWindowsReduceApp {
    public static void main(String[] args) throws Exception {
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        DataStreamSource<String> data = env.socketTextStream("192.168.226.128", 9999);
        data.flatMap(new FlatMapFunction<String, Tuple2<Integer, Integer>>() {
            public void flatMap(String value, Collector<Tuple2<Integer, Integer>> out) throws Exception {
                String[] token = value.toLowerCase().split(",");
                for (String s : token) {
                    if (token.length > 0) {
                        out.collect(new Tuple2<Integer, Integer>(1, Integer.parseInt(s)));

                    }
                }
            }
        }).keyBy(0).timeWindow(Time.seconds(5))
                .reduce(new ReduceFunction<Tuple2<Integer, Integer>>() {
                    public Tuple2<Integer, Integer> reduce(Tuple2<Integer, Integer> value1, Tuple2<Integer, Integer> value2) throws Exception {
                        return new Tuple2<Integer, Integer>(value1.f0, value1.f1 + value2.f1);
                    }
                })
                .print().setParallelism(1);


        env.execute("xx");
    }
}
