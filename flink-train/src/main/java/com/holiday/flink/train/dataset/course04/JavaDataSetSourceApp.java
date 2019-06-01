package com.holiday.flink.train.dataset.course04;

import org.apache.flink.api.java.ExecutionEnvironment;

import java.util.ArrayList;
import java.util.List;

public class JavaDataSetSourceApp {
    public static void main(String[] args) throws Exception {
        ExecutionEnvironment env = ExecutionEnvironment.getExecutionEnvironment();
        // fromCollection(env);
        textFile(env);
    }

    public static void textFile(ExecutionEnvironment env) throws Exception {
        //读取文件
        String filePath = "data/hello";
        env.readTextFile(filePath).print();
        System.out.println("文件.....");
        //读取文件夹
        filePath = "data";
        env.readTextFile(filePath).print();
    }

    public static void fromCollection(ExecutionEnvironment env) throws Exception {
        List<Integer> list = new ArrayList<Integer>();
        for (int i = 1; i <= 10; i++) {
            list.add(i);
        }
        env.fromCollection(list).print();
    }
}
