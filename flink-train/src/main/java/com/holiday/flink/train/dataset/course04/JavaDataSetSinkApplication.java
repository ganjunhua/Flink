package com.holiday.flink.train.dataset.course04;

import org.apache.flink.api.java.ExecutionEnvironment;
import org.apache.flink.api.java.operators.DataSource;
import org.apache.flink.core.fs.FileSystem;

import java.util.ArrayList;
import java.util.List;

public class JavaDataSetSinkApplication {
    public static void main(String[] args) throws Exception {
        ExecutionEnvironment env = ExecutionEnvironment.getExecutionEnvironment();
        List<Integer> list = new ArrayList<Integer>();
        for (int i = 1; i <= 10; i++) list.add(i);
        DataSource data = env.fromCollection(list);
        String filePath="data/sink/java";
        data.writeAsText(filePath, FileSystem.WriteMode.OVERWRITE);
        env.execute(Thread.currentThread().getStackTrace()[1].getClassName());
    }
}
