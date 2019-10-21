package DataSet.flink.datasource;

import org.apache.flink.api.java.ExecutionEnvironment;

import java.util.ArrayList;
import java.util.List;

public class JavaDataSetDataSourceApp {
    public static void main(String[] args) throws Exception {
        ExecutionEnvironment env = ExecutionEnvironment.getExecutionEnvironment();

        // fromCollection(env);
        textFile(env);
    }

    public static void fromCollection(ExecutionEnvironment env) throws Exception {
        List<Integer> list = new ArrayList<Integer>();
        for (int i = 1; i <= 10; i++) {
            list.add(i);
        }
        env.fromElements(list).print();
    }

    public static void textFile(ExecutionEnvironment env) throws Exception {
        String dataPath = "data";
        env.readTextFile(dataPath).print();
    }


}
