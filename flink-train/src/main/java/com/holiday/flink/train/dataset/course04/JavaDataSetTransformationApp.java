package com.holiday.flink.train.dataset.course04;

import org.apache.flink.api.common.functions.*;
import org.apache.flink.api.common.operators.Order;
import org.apache.flink.api.java.ExecutionEnvironment;
import org.apache.flink.api.java.operators.DataSource;
import org.apache.flink.api.java.tuple.Tuple2;
import org.apache.flink.api.java.tuple.Tuple3;
import org.apache.flink.util.Collector;


import java.util.ArrayList;
import java.util.List;

public class JavaDataSetTransformationApp {

    public static void main(String[] args) throws Exception {
        final ExecutionEnvironment env = ExecutionEnvironment.getExecutionEnvironment();
        crossFunction(env);
    }

    public static void crossFunction(ExecutionEnvironment env) throws Exception {
        // 笛卡尔集
        List<String> info = new ArrayList<String>();
        info.add("曼联");
        info.add("曼城");
        List<String> info2 = new ArrayList<String>();
        info2.add("3");
        info2.add("1");
        info2.add("0");
        DataSource data1 = env.fromCollection(info);
        DataSource data2 = env.fromCollection(info2);
        data1.cross(data2).print();
    }

    public static void joinFunction(ExecutionEnvironment env) throws Exception {
        List<Tuple2<Integer, String>> info1 = new ArrayList<Tuple2<Integer, String>>();
        info1.add(new Tuple2<Integer, String>(1, "h1"));
        info1.add(new Tuple2<Integer, String>(2, "h2"));
        info1.add(new Tuple2<Integer, String>(3, "h3"));
        info1.add(new Tuple2<Integer, String>(4, "h4"));
        List<Tuple2<Integer, String>> info2 = new ArrayList<Tuple2<Integer, String>>();
        info2.add(new Tuple2<Integer, String>(1, "bj"));
        info2.add(new Tuple2<Integer, String>(2, "sh"));
        info2.add(new Tuple2<Integer, String>(3, "gz"));
        info2.add(new Tuple2<Integer, String>(5, "sz"));
        DataSource data1 = env.fromCollection(info1);
        DataSource data2 = env.fromCollection(info2);
        data1.join(data2).where(0).equalTo(0).print();
    }

    public static void distinctFunction(ExecutionEnvironment env) throws Exception {
        List<String> info = new ArrayList<String>();
        info.add("hadoop,spark");
        info.add("hadoop,spark");
        info.add("hadoop,flink");
        DataSource data = env.fromCollection(info);
        data.flatMap(new FlatMapFunction<String, String>() {

            public void flatMap(String s, Collector<String> collector) throws Exception {
                for (String value : s.split(",")) {
                    collector.collect(value);
                }
            }
        }).distinct().print();
    }

    public static void flatMapFunction(ExecutionEnvironment env) throws Exception {
        List<String> info = new ArrayList<String>();
        info.add("hadoop,spark");
        info.add("hadoop,spark");
        info.add("hadoop,flink");
        DataSource data = env.fromCollection(info);
        // wordcount
        data.flatMap(new FlatMapFunction<String, String>() {
            public void flatMap(String s, Collector<String> collector) throws Exception {
                String[] inputs = s.split(",");
                for (String input : inputs) {
                    collector.collect(input);
                }
            }
        }).map(new MapFunction<String, Tuple2<String, Integer>>() {
            public Tuple2<String, Integer> map(String s) throws Exception {
                return new Tuple2<String, Integer>(s, 1);
            }
        }).groupBy(0).sum(1).print();
    }

    public static void firstFunction(ExecutionEnvironment env) throws Exception {
        List<Tuple2<Integer, String>> info = new ArrayList<Tuple2<Integer, String>>();
        info.add(new Tuple2(1, "hadoop"));
        info.add(new Tuple2(1, "spark"));
        info.add(new Tuple2(1, "flink"));
        info.add(new Tuple2(2, "java"));
        info.add(new Tuple2(2, "spring boot"));
        info.add(new Tuple2(3, "linux"));
        info.add(new Tuple2(4, "vue"));
        DataSource data = env.fromCollection(info);
        data.first(3).print();
        data.groupBy(0).first(2).print();
        data.groupBy(0).sortGroup(1, Order.DESCENDING).first(2).print();
    }

    public static void mapPartitonFunction(ExecutionEnvironment env) throws Exception {
        List<Integer> list = new ArrayList<Integer>();
        for (int i = 1; i <= 100; i++) list.add(i);
        env.fromCollection(list).setParallelism(6)
                .mapPartition(new MapPartitionFunction<Integer, Integer>() {
                    public void mapPartition(Iterable<Integer> iterable, Collector<Integer> collector) throws Exception {
                        String connection = DBUtils.getConection();
                        System.out.println("connection" + connection);
                        DBUtils.returnConnection(connection);
                    }
                }).print();

    }

    public static void filterFunction(ExecutionEnvironment env) throws Exception {
        List<Integer> list = new ArrayList<Integer>();
        for (int i = 1; i <= 10; i++) list.add(i);
        env.fromCollection(list).map(new MapFunction<Integer, Integer>() {

            public Integer map(Integer integer) throws Exception {
                return integer + 4;
            }
        }).filter(new FilterFunction<Integer>() {
            public boolean filter(Integer integer) throws Exception {
                return integer > 6;
            }
        }).print();
    }

    public static void mapFunction(ExecutionEnvironment env) throws Exception {
        List<Integer> list = new ArrayList<Integer>();
        for (int i = 1; i <= 10; i++) list.add(i);
        DataSource<Integer> data = env.fromCollection(list);
        data.map(new MapFunction<Integer, Integer>() {
            public Integer map(Integer integer) throws Exception {
                return integer + 1;
            }
        }).print();

    }
}
