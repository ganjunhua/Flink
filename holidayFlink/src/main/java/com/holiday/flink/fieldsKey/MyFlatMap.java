package com.holiday.flink.fieldsKey;

import org.apache.flink.api.common.functions.FlatMapFunction;
import org.apache.flink.util.Collector;

public class MyFlatMap implements FlatMapFunction<String, StreamingWCJavaAppSelectKey.WC> {
    public void flatMap(String s, Collector<StreamingWCJavaAppSelectKey.WC> collector) throws Exception {
        //对每一行数据进行拆分，返回数组
        String[] words = s.toLowerCase().split(",");
        //对返回的数组进行处理
        for (String key : words) {
            if (key.length() > 0) {
                //处理完后将数据返回出去,结果：(holiday,1)  返回是一个wc类
                collector.collect(new StreamingWCJavaAppSelectKey.WC(key, 1));
            }
        }
    }
}
