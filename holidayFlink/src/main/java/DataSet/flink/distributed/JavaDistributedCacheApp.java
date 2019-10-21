package DataSet.flink.distributed;

import org.apache.commons.io.FileUtils;
import org.apache.flink.api.common.functions.RichMapFunction;
import org.apache.flink.api.java.ExecutionEnvironment;
import org.apache.flink.api.java.operators.DataSource;
import org.apache.flink.configuration.Configuration;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class JavaDistributedCacheApp {
    public static void main(String[] args) throws Exception {
        ExecutionEnvironment env = ExecutionEnvironment.getExecutionEnvironment();

        String filePath = "data/wc";
        env.registerCachedFile(filePath, "jholiday");

        DataSource<String> data1 = env.fromElements("h1", "h2");
        data1.map(new RichMapFunction<String, String>() {
            List<String> list = new ArrayList<String>();
            @Override
            public void open(Configuration parameters) throws Exception {
                super.open(parameters);
                File jFile = getRuntimeContext().getDistributedCache().getFile("jholiday");
                List<String> strings = FileUtils.readLines(jFile);
                for(String s:strings){
                    list.add(s);
                    System.out.println(s);
                }
            }

            public String map(String value) throws Exception {
                return value;
            }
        }).print();
    }
}
