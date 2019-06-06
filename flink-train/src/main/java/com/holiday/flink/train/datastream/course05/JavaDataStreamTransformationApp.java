package com.holiday.flink.train.datastream.course05;

import org.apache.flink.api.common.functions.FilterFunction;
import org.apache.flink.api.common.functions.MapFunction;
import org.apache.flink.streaming.api.collector.selector.OutputSelector;
import org.apache.flink.streaming.api.datastream.DataStreamSource;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;

import java.util.ArrayList;
import java.util.List;

public class JavaDataStreamTransformationApp {
    public static void main(String[] args) throws Exception {

        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        //filterFunction(env);
        //unionFunction(env);
        splitSelectFunction(env);
        env.execute("JavaDataStreamTransformationApp");
    }

    public static void splitSelectFunction(StreamExecutionEnvironment env) {
        DataStreamSource data1 = env.addSource(new CustomNonParallelSourceFunctionApp());
        data1.split(new OutputSelector<Long>() {

            public Iterable<String> select(Long value) {
                List<String> list = new ArrayList<String>();
                if (value % 2 == 0) {
                    list.add("even");
                } else list.add("odd");
                return list;
            }
        }).select("odd").print();
    }

    public static void unionFunction(StreamExecutionEnvironment env) {
        DataStreamSource data1 = env.addSource(new CustomNonParallelSourceFunctionApp());
        DataStreamSource data2 = env.addSource(new CustomNonParallelSourceFunctionApp());
        data1.union(data2).print();
    }

    public static void filterFunction(StreamExecutionEnvironment env) {
        DataStreamSource data = env.addSource(new JavaCustomNonParallelSourceApplication());
        data.map(new MapFunction<Long, Long>() {
            public Long map(Long value) throws Exception {
                return value;
            }
        }).filter(new FilterFunction<Long>() {
            public boolean filter(Long value) throws Exception {
                return value % 2 == 0;
            }
        }).print();
    }
}
