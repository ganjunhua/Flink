package DataSet.flink.train;

import org.apache.flink.api.common.functions.FlatMapFunction;
import org.apache.flink.api.java.ExecutionEnvironment;
import org.apache.flink.api.java.operators.AggregateOperator;
import org.apache.flink.api.java.operators.DataSource;
import org.apache.flink.api.java.tuple.Tuple2;
import org.apache.flink.util.Collector;


public class BatchWCJavaAPp {
    public static void main(String[] args) throws Exception {

        String input = "data/wc";
        // 1、得到上下文
        ExecutionEnvironment evn = ExecutionEnvironment.getExecutionEnvironment();
        // 2、读取数据 获取的数据行的顺序是乱序的
        DataSource<String> text = evn.readTextFile(input);
        //3、转换操作  入参的数据类型：String，出参的数据类型：Tuple2<String, Integer>
        AggregateOperator<Tuple2<String, Integer>> sum = text.flatMap(new FlatMapFunction<String, Tuple2<String, Integer>>() {
            //String word：这个就是入参传进来的每一行数据，collector：这个就是返回实际的数据
            public void flatMap(String word, Collector<Tuple2<String, Integer>> collector) throws Exception {
                //对每一行数据进行拆分，返回数组
                String[] words = word.toLowerCase().split(",");
                //对返回的数组进行处理
                for (String key : words) {
                    if (key.length() > 0) {
                        //处理完后将数据返回出去,结果：(holiday,1)
                        collector.collect(new Tuple2<String, Integer>(key, 1));
                    }
                }
            }
        }).groupBy(0).sum(1);// 对返回的结果对下标0进行分组 (holiday,1)，对下标1进行求和

        sum.print();

    }
}
