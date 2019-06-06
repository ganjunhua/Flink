package com.holiday.flink.train.datastream.course05;

import org.apache.flink.api.common.functions.MapFunction;
import org.apache.flink.streaming.api.datastream.DataStreamSource;
import org.apache.flink.streaming.api.datastream.SingleOutputStreamOperator;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;

public class JavaCustSinkToMySQL {
    public static void main(String[] args) throws Exception {

        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        DataStreamSource<String> source = env.socketTextStream("holiday-1", 9999);
        //new MapFunction<String, Students>  String:入参， Students 出参
        SingleOutputStreamOperator<Students> data = source.map(new MapFunction<String, Students>() {
            public Students map(String value) throws Exception {
                String[] splits = value.split(",");
                Students stu = new Students();
                stu.setId(Integer.parseInt(splits[0]));
                stu.setName(splits[1]);
                stu.setAge(Integer.parseInt(splits[2]));
                return stu;
            }
        });
        data.addSink(new SinkToMySQL());
        env.execute(Thread.currentThread().getName());
    }
}
