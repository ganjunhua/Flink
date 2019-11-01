package DataStream.flink.windows;

import org.apache.flink.api.common.functions.FlatMapFunction;
import org.apache.flink.api.common.functions.ReduceFunction;
import org.apache.flink.api.java.tuple.Tuple;
import org.apache.flink.api.java.tuple.Tuple2;
import org.apache.flink.streaming.api.datastream.DataStreamSource;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.functions.windowing.ProcessWindowFunction;
import org.apache.flink.streaming.api.windowing.time.Time;
import org.apache.flink.streaming.api.windowing.windows.TimeWindow;
import org.apache.flink.util.Collector;

public class JavaWindowsProcessApp {
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
                .process(new ProcessWindowFunction<Tuple2<Integer, Integer>, Object, Tuple, TimeWindow>() {
                    public void process(Tuple tuple, Context context, Iterable<Tuple2<Integer, Integer>> elements, Collector<Object> out) throws Exception {
                        System.out.println("----------");
                        long count = 0;
                        for (Tuple2<Integer, Integer> in : elements) {

                            count += in.f1;
                        }
                        out.collect("window" + context.window() + "count:" + count);
                    }
                })
                .print().setParallelism(1);


        env.execute("xx");
    }
}
