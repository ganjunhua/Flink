package com.holiday.flink.train.dataset.course04;

import org.apache.flink.api.common.functions.FilterFunction;
import org.apache.flink.api.common.functions.MapFunction;
import org.apache.flink.api.common.functions.MapPartitionFunction;
import org.apache.flink.api.java.ExecutionEnvironment;
import org.apache.flink.api.java.operators.DataSource;
import org.apache.flink.util.Collector;

import java.util.ArrayList;
import java.util.List;

public class JavaDataSetTransformationApp {
    public static void main(String[] args) throws Exception {
        final ExecutionEnvironment env = ExecutionEnvironment.getExecutionEnvironment();
        mapPartitonFunction(env);
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
