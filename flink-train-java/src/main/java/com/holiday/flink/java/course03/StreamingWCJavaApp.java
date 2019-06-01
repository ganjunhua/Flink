package com.holiday.flink.java.course03;

import org.apache.flink.api.common.functions.FlatMapFunction;
import org.apache.flink.api.java.tuple.Tuple2;
import org.apache.flink.streaming.api.datastream.DataStreamSource;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.windowing.time.Time;
import org.apache.flink.util.Collector;

/**
 * 使用java api 来开发Flink的流式处理应用 程序
 * <p>
 * wc统计的数据我们源自于socker
 */
public class StreamingWCJavaApp {
    public static void main(String[] args) throws Exception {
        // step1:获取执行环境
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        // step2:读取 数据
        DataStreamSource<String> text = env.socketTextStream("holiday-7-1", 9999);
        // step3:转换
        text.flatMap(new FlatMapFunction<String, WC>() {
            @Override
            public void flatMap(String value, Collector<WC> collector) throws Exception {
                String regex = " ";
                String[] tokens = value.toLowerCase().split(regex);
                for (String token : tokens) {
                    if (token.length() > 0) {
                        collector.collect(new WC(token, 1));
                    }
                }
            }
        }).keyBy("word")
                .timeWindow(Time.seconds(5))
                .sum("count")
                .print()
                .setParallelism(1);
        // step4:流式处理一定要加这行代码
        env.execute("StreamingWCJavaApp");

    }

    public static class WC {
        private String word;
        private int count;

        public WC(String word, int count) {
            this.word = word;
            ;
            this.count = count;
        }

        public WC() {
        }

        @Override
        public String toString() {
            return "WC{" +
                    "word='" + word + '\'' +
                    ", count=" + count +
                    '}';
        }

        public void setWord(String word) {
            this.word = word;
        }

        public void setCount(int count) {
            this.count = count;
        }

        public String getWord() {
            return word;
        }

        public int getCount() {
            return count;
        }
    }
}
