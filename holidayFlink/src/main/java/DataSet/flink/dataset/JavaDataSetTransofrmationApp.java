package DataSet.flink.dataset;

import org.apache.flink.api.common.functions.*;
import org.apache.flink.api.java.ExecutionEnvironment;
import org.apache.flink.api.java.operators.DataSource;
import org.apache.flink.api.java.tuple.Tuple2;
import org.apache.flink.api.java.tuple.Tuple3;
import org.apache.flink.util.Collector;

import java.util.ArrayList;
import java.util.List;

import static java.sql.DriverManager.println;

public class JavaDataSetTransofrmationApp {
    public static void main(String[] args) throws Exception {
        ExecutionEnvironment env = ExecutionEnvironment.getExecutionEnvironment();
        crossFunction(env);
    }

    public static void crossFunction(ExecutionEnvironment env) throws Exception {
        List<String> info1 = new ArrayList<String>();
        info1.add("北京");
        info1.add("上海");

        List<String> info2 = new ArrayList<String>();
        info2.add("3");
        info2.add("1");
        info2.add("0");

        DataSource<String> data1 = env.fromCollection(info1);
        DataSource<String> data2 = env.fromCollection(info2);

        data1.cross(data2).print();

    }

    public static void joinFunction(ExecutionEnvironment env) throws Exception {
        List<Tuple2<Integer, String>> info = new ArrayList<Tuple2<Integer, String>>();
        info.add(new Tuple2<Integer, String>(1, "holiday"));
        info.add(new Tuple2<Integer, String>(2, "holiday1"));
        info.add(new Tuple2<Integer, String>(3, "holiday3"));
        info.add(new Tuple2<Integer, String>(4, "holiday4"));

        List<Tuple2<Integer, String>> info1 = new ArrayList<Tuple2<Integer, String>>();
        info1.add(new Tuple2<Integer, String>(1, "北京"));
        info1.add(new Tuple2<Integer, String>(2, "上海"));
        info1.add(new Tuple2<Integer, String>(3, "深圳"));
        info1.add(new Tuple2<Integer, String>(5, "广州"));
        DataSource<Tuple2<Integer, String>> data1 = env.fromCollection(info);
        DataSource<Tuple2<Integer, String>> data2 = env.fromCollection(info1);

        data1.fullOuterJoin(data2).where(0).equalTo(0).with(new JoinFunction<Tuple2<Integer, String>, Tuple2<Integer, String>, Tuple3<Integer, String, String>>() {
            public Tuple3<Integer, String, String> join(Tuple2<Integer, String> first, Tuple2<Integer, String> second) throws Exception {
                if (first == null) {
                    return new Tuple3<Integer, String, String>(second.f0, second.f1, "--");
                } else if (second == null) {
                    return new Tuple3<Integer, String, String>(first.f0, first.f1, "---");
                } else return new Tuple3<Integer, String, String>(first.f0, first.f1, second.f1);
            }
        }).print();

    }

    public static void flatMapFunction(ExecutionEnvironment env) throws Exception {
        List<String> info = new ArrayList<String>();
        info.add("holiday,spark");
        info.add("holiday,flink");
        info.add("holiday,scala");
        DataSource<String> data = env.fromCollection(info);
        data.flatMap(new FlatMapFunction<String, String>() {
            public void flatMap(String value, Collector<String> out) throws Exception {
                String[] split = value.split(",");
                for (String s : split) {
                    out.collect(s);
                }
            }
        }).map(new MapFunction<String, Tuple2<String, Integer>>() {
            public Tuple2<String, Integer> map(String value) throws Exception {
                return new Tuple2<String, Integer>(value, 1);
            }
        }).groupBy(0).sum(1).print();
    }

    public static void firstFunction(ExecutionEnvironment env) throws Exception {
        List<Tuple2<Integer, String>> info = new ArrayList<Tuple2<Integer, String>>();
        info.add(new Tuple2<Integer, String>(1, "holiday1"));
        info.add(new Tuple2(2, "holiday2"));
        info.add(new Tuple2(2, "aoliday2"));
        info.add(new Tuple2(3, "holiday3"));
        info.add(new Tuple2(3, "aoliday3"));
        info.add(new Tuple2(4, "holiday4"));
        info.add(new Tuple2(5, "holiday5"));
        DataSource<Tuple2<Integer, String>> data = env.fromCollection(info);
        data.first(3).print();
    }

    public static void mapPartitionFunction(ExecutionEnvironment env) throws Exception {
        List<String> list = new ArrayList<String>();
        for (int i = 1; i <= 100; i++) {
            list.add("list:" + i);
        }
        DataSource<String> data = env.fromCollection(list).setParallelism(4);
        data.mapPartition(new MapPartitionFunction<String, Object>() {
            public void mapPartition(Iterable<String> values, Collector<Object> out) throws Exception {
                String conection = DBUtils.getConection();
                System.out.println(conection);
                println(conection + "......");
                DBUtils.returnConnection(conection);
            }
        }).print();
    }

    public static void filterFunction(ExecutionEnvironment env) throws Exception {
        List<Integer> list = new ArrayList<Integer>();
        for (int i = 1; i <= 10; i++) {
            list.add(i);
        }
        DataSource<Integer> data = env.fromCollection(list);
        data.map(new MapFunction<Integer, Integer>() {
            public Integer map(Integer input) throws Exception {
                return input + 1;
            }
        }).filter(new FilterFunction<Integer>() {
            public boolean filter(Integer value) throws Exception {
                return value > 3;
            }
        }).print();
    }

    public static void mapFunction(ExecutionEnvironment env) throws Exception {
        List<Integer> list = new ArrayList<Integer>();
        for (int i = 1; i <= 10; i++) {
            list.add(i);
        }
        DataSource<Integer> data = env.fromCollection(list);
        data.map(new MapFunction<Integer, Integer>() {
            public Integer map(Integer input) throws Exception {
                return input + 1;
            }
        }).print();

    }
}
