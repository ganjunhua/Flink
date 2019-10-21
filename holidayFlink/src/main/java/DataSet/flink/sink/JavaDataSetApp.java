package DataSet.flink.sink;

import org.apache.flink.api.java.ExecutionEnvironment;
import org.apache.flink.api.java.operators.DataSource;
import org.apache.flink.core.fs.FileSystem;

import java.util.ArrayList;
import java.util.List;

public class JavaDataSetApp {
    public static void main(String[] args) throws Exception {
        ExecutionEnvironment env = ExecutionEnvironment.getExecutionEnvironment();
        List<Integer> list = new ArrayList<Integer>();
        for (int i = 1; i <= 10; i++) {
            list.add(i);
        }
        DataSource<Integer> data1 = env.fromCollection(list);
        String filePath = "data/sink/java";
        data1.writeAsText(filePath, FileSystem.WriteMode.OVERWRITE).setParallelism(3);
        System.out.println(Thread.currentThread().getStackTrace()[1].getLineNumber());
        env.execute(Thread.currentThread().getName());
    }
}
