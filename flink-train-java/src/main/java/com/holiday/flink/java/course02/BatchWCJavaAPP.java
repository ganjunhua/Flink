package com.holiday.flink.java.course02;

import org.apache.flink.api.common.functions.FlatMapFunction;
import org.apache.flink.api.java.ExecutionEnvironment;
import org.apache.flink.api.java.operators.DataSource;
import org.apache.flink.api.java.tuple.Tuple2;
import org.apache.flink.util.Collector;


/**
 * 使用java api 来开发Flink的批处理应用 程序
 */
public class BatchWCJavaAPP {
    public static void main(String[] args) throws Exception {
        String input = "data/wordCount";
        // step1 :获取行环境
        ExecutionEnvironment env = ExecutionEnvironment.getExecutionEnvironment();
        //step2:  read data
        DataSource<String> text = env.readTextFile(input);
        //step3:transform
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
        }).groupBy(0).sum(1).print();
        env.execute(" batch for java");
    }
}
