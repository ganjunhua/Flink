package DataSet.flink.fieldsKey;

import org.apache.flink.api.common.functions.FlatMapFunction;
import org.apache.flink.streaming.api.datastream.DataStreamSource;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.windowing.time.Time;
import org.apache.flink.util.Collector;

public class StreamingWCJavaApp {
    public static void main(String[] args) throws Exception {

        // 1、获取上下文
        StreamExecutionEnvironment evn = StreamExecutionEnvironment.getExecutionEnvironment();
        //2、读取数据
        DataStreamSource<String> text = evn.socketTextStream("192.168.226.128", 9999);
        //3、操作转换
        //使用类进行指定字段分组
        text.flatMap(new FlatMapFunction<String, WC>() {
            public void flatMap(String s, Collector<WC> collector) throws Exception {
                //对每一行数据进行拆分，返回数组
                String[] words = s.toLowerCase().split(",");
                //对返回的数组进行处理
                for (String key : words) {
                    if (key.length() > 0) {
                        //处理完后将数据返回出去,结果：(holiday,1)  返回是一个wc类
                        collector.collect(new WC(key, 1));
                    }
                }
            }
        }).keyBy("word").timeWindow(Time.seconds(5)).sum("count").print();//这里就可以指定类定义的变量字段了

        //4、执行代码
        evn.execute();
    }

    public static class WC {
        private String word;
        private int count;

        public WC() {
        }

        public WC(String word, int count) {
            this.word = word;
            this.count = count;
        }

        @Override
        public String toString() {
            return "WC{" +
                    "word='" + word + '\'' +
                    ", count=" + count +
                    '}';
        }

        public String getWord() {
            return word;
        }

        public void setWord(String word) {
            this.word = word;
        }

        public int getCount() {
            return count;
        }

        public void setCount(int count) {
            this.count = count;
        }
    }
}
