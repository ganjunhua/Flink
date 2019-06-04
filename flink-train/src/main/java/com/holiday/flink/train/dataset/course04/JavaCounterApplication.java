package com.holiday.flink.train.dataset.course04;

import akka.remote.artery.ReusableInboundEnvelope;
import com.sun.org.apache.xml.internal.dtm.ref.sax2dtm.SAX2DTM2;
import org.apache.flink.api.common.JobExecutionResult;
import org.apache.flink.api.common.accumulators.LongCounter;
import org.apache.flink.api.common.functions.RichMapFunction;
import org.apache.flink.api.java.DataSet;
import org.apache.flink.api.java.ExecutionEnvironment;
import org.apache.flink.api.java.operators.DataSource;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.core.fs.FileSystem;

public class JavaCounterApplication {
    public static void main(String[] args) throws Exception {
        ExecutionEnvironment env = ExecutionEnvironment.getExecutionEnvironment();
        DataSource data = env.fromElements("hadoop", "spark", "flink", "flink");
        DataSet<String> info = data.map(new RichMapFunction<String, String>() {
            //定义计数器
            LongCounter counter = new LongCounter();

            @Override
            public void open(Configuration parameters) throws Exception {
                //注册计数器
                getRuntimeContext().addAccumulator("counter", counter);

            }

            public String map(String value) throws Exception {
                counter.add(1);
                return value;
            }
        });
        String filePath = "data/sink/counter/java";
        info.writeAsText(filePath, FileSystem.WriteMode.OVERWRITE).setParallelism(3);
        JobExecutionResult result =  env.execute(Thread.currentThread().getName());
        //获取计数器结果
        System.out.println(result.getAccumulatorResult("counter"));


    }
}
