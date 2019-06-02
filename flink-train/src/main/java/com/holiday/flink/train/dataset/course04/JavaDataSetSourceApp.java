package com.holiday.flink.train.dataset.course04;

import org.apache.flink.api.java.ExecutionEnvironment;
import org.apache.flink.api.java.io.CsvReader;
import org.apache.flink.configuration.Configuration;

import java.util.ArrayList;
import java.util.List;

public class JavaDataSetSourceApp {
    public static void main(String[] args) throws Exception {
        ExecutionEnvironment env = ExecutionEnvironment.getExecutionEnvironment();
        // fromCollection(env);
        readRecursvieFile(env);
    }

    public static void csvFile(ExecutionEnvironment env) throws Exception {
        //读取csv文件
        String filePath = "data/people.csv";
        env.readCsvFile(filePath)
                .ignoreFirstLine()
                // includeFields(true, false, true) 选择需要哪些字段，true表示需要，false表示不需要
                .includeFields(true, false, true)
                //根据 true 选择对应的列
                .pojoType(People.class, "name", "work")
                .print();
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

    public static void readRecursvieFile(ExecutionEnvironment env) throws Exception {
        //递归读取 文件夹
        String filePath = "data";
        //创建递归配置对象
        Configuration paraments = new Configuration();
        //设置递归属性
        paraments.setBoolean("recursive.file.enumeration", true);
        env.readTextFile(filePath).withParameters(paraments).print();
    }

    public static void fromCollection(ExecutionEnvironment env) throws Exception {
        List<Integer> list = new ArrayList<Integer>();
        for (int i = 1; i <= 10; i++) {
            list.add(i);
        }
        env.fromCollection(list).print();
    }
}
