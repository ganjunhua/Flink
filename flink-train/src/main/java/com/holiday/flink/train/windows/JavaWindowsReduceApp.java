package com.holiday.flink.train.windows;

import org.apache.flink.api.common.functions.FlatMapFunction;
import org.apache.flink.api.common.functions.ReduceFunction;
import org.apache.flink.api.java.tuple.Tuple2;
import org.apache.flink.streaming.api.datastream.DataStreamSource;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.windowing.time.Time;
import org.apache.flink.util.Collector;

/**
 * 滚动窗口+reducebykey
 *
 */
public class JavaWindowsReduceApp {
    public static void main(String[] args) throws Exception {
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        DataStreamSource<String> text = env.socketTextStream("holiday-7-1", 9999);
        text.flatMap(new FlatMapFunction<String, Tuple2<Integer, Integer>>() {

            public void flatMap(String s, Collector<Tuple2<Integer, Integer>> collector) throws Exception {
                String[] tokens = s.toLowerCase().split(",");
                for (String token : tokens) {
                    if (token.length() > 0) collector.collect(new Tuple2<Integer, Integer>(1, Integer.parseInt(token)));
                }
            }
        }).keyBy(0).timeWindow(Time.seconds(5))
                .reduce(new ReduceFunction<Tuple2<Integer, Integer>>() {
                    public Tuple2<Integer, Integer> reduce(Tuple2<Integer, Integer> t1, Tuple2<Integer, Integer> t2) throws Exception {
                        return new Tuple2<Integer, Integer>(t1.f0, t1.f1 + t2.f1);
                    }
                })
                .print()
                .setParallelism(1);
        env.execute(Thread.currentThread().getName());
    }
}
