package com.holiday.flink.java.course02;

import org.apache.flink.api.common.functions.FlatMapFunction;
import org.apache.flink.api.java.tuple.Tuple2;
import org.apache.flink.api.java.utils.ParameterTool;
import org.apache.flink.streaming.api.datastream.DataStreamSource;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.windowing.time.Time;
import org.apache.flink.util.Collector;

/**
 * 使用java api 来开发Flink的流式处理应用 程序
 * <p>
 * wc统计的数据我们源自于socker
 */
public class StreamingWCJava02App {
    public static void main(String[] args) throws Exception {
        //获取参数
        int port = 0;
        //这个是flink的一个工具，可以从参数里面获取值
        try {
            ParameterTool tool = ParameterTool.fromArgs(args);
            int port1 = tool.getInt("port");
        } catch (Exception e) {
            System.err.println("端口未设置，使用默认端口9999");
            port = 9999;
        }

        // step1:获取执行环境
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        // step2:读取 数据
        DataStreamSource<String> text = env.socketTextStream("Holiday", 9999);
        // step3:转换
        text.flatMap(new FlatMapFunction<String, Tuple2<String, Integer>>() {
            @Override
            public void flatMap(String value, Collector<Tuple2<String, Integer>> collector) throws Exception {
                String[] tokens = value.toLowerCase().split(" ");
                for (String token : tokens) {
                    if (token.length() > 0) {
                        collector.collect(new Tuple2<String, Integer>(token, 1));
                    }
                }
            }
        }).keyBy(0).timeWindow(Time.seconds(5)).sum(1).print();
        // step4:流式处理一定要加这行代码
        env.execute("StreamingWCJavaApp");

    }
}
