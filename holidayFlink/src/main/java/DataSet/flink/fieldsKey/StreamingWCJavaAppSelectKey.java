package DataSet.flink.fieldsKey;

import org.apache.flink.api.java.functions.KeySelector;
import org.apache.flink.streaming.api.datastream.DataStreamSource;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.windowing.time.Time;

public class StreamingWCJavaAppSelectKey {
    public static void main(String[] args) throws Exception {

        // 1、获取上下文
        StreamExecutionEnvironment evn = StreamExecutionEnvironment.getExecutionEnvironment();
        //2、读取数据
        DataStreamSource<String> text = evn.socketTextStream("192.168.226.128", 9999);
        //3、操作转换
        //使用类进行指定字段分组
        text.flatMap(new MyFlatMap()).keyBy(new KeySelector<WC, String>() {
            // key 选择 分组
            public String getKey(WC wc) throws Exception {
                return wc.word;
            }
        }).timeWindow(Time.seconds(5)).sum("count").print();//这里就可以指定类定义的变量字段了

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
