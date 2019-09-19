package com.holiday.flink.train;

import org.apache.flink.api.common.functions.FlatMapFunction;
import org.apache.flink.api.java.tuple.Tuple2;
import org.apache.flink.streaming.api.datastream.DataStreamSource;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.windowing.time.Time;
import org.apache.flink.util.Collector;

public class StreamingWCJavaApp {
    public static void main(String[] args) throws Exception {

        // 1、获取上下文
        StreamExecutionEnvironment evn = StreamExecutionEnvironment.getExecutionEnvironment();
        //2、读取数据
        DataStreamSource<String> text = evn.socketTextStream("192.168.226.128", 9993);
        //3、操作转换
        text.flatMap(new FlatMapFunction<String, Tuple2<String, Integer>>() {

            public void flatMap(String s, Collector<Tuple2<String, Integer>> collector) throws Exception {
                //对每一行数据进行拆分，返回数组
                String[] words = s.toLowerCase().split(",");
                //对返回的数组进行处理
                for (String key : words) {
                    if (key.length() > 0) {
                        //处理完后将数据返回出去,结果：(holiday,1)
                        collector.collect(new Tuple2<String, Integer>(key, 1));
                    }
                }
            }
        }).keyBy(0).timeWindow(Time.seconds(5)).sum(1).print();
        //4、执行代码
        evn.execute();
    }
}
