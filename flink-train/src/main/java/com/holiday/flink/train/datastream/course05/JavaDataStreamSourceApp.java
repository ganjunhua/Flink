package com.holiday.flink.train.datastream.course05;

import org.apache.flink.streaming.api.datastream.DataStreamSource;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;

public class JavaDataStreamSourceApp {
    public static void main(String[] args) throws Exception {
        //step 1: 获取执行环境
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        // step2:执行 transformation
        //sockerFunction(env);
        // nonParallelSourceApplication(env);
        //parallelSourceApplication(env);
        richParallelSourceApp(env);
        //step3:执行代码
        env.execute(Thread.currentThread().getName());
    }

    public static void richParallelSourceApp(StreamExecutionEnvironment env) {
        DataStreamSource data = env.addSource(new JavaCustomRichParallelSourceApp());
        data.print();
    }

    public static void parallelSourceApplication(StreamExecutionEnvironment env) {
        DataStreamSource data = env.addSource(new JavaCustomParallelSourceApplication());
        data.print();
    }

    public static void nonParallelSourceApplication(StreamExecutionEnvironment env) {
        DataStreamSource data = env.addSource(new JavaCustomNonParallelSourceApplication());
        data.print();
    }

    public static void sockerFunction(StreamExecutionEnvironment env) {
        DataStreamSource data = env.socketTextStream("holiday-1", 9999);
        data.print().setParallelism(1);

    }
}
