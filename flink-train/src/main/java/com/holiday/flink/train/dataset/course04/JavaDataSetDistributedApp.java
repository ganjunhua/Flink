package com.holiday.flink.train.dataset.course04;

import org.apache.commons.io.FileUtils;
import org.apache.flink.api.common.functions.RichMapFunction;
import org.apache.flink.api.java.ExecutionEnvironment;
import org.apache.flink.api.java.operators.DataSource;
import org.apache.flink.configuration.Configuration;

import java.io.File;
import java.util.ArrayList;
import java.util.List;


public class JavaDataSetDistributedApp {
    public static void main(String[] args) throws Exception {
        ExecutionEnvironment env = ExecutionEnvironment.getExecutionEnvironment();
        String filePath = "data/hello";
        env.registerCachedFile(filePath, "holiday-java");
        DataSource data = env.fromElements("hadoop", "flink", "xx");
        //保存分布式文件值

        data.map(new RichMapFunction<String, String>() {
            List<String> list = new ArrayList<String>();

            @Override
            public void open(Configuration parameters) throws Exception {
                File file = getRuntimeContext().getDistributedCache().getFile("holiday-java");
                List<String> lines = FileUtils.readLines(file);
                for (String line : lines) {
                    list.add(line);
                    System.out.println(line);
                }
            }

            public String map(String value) throws Exception {
                return value;
            }
        }).print();

    }
}
