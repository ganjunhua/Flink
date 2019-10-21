package DataSet.flink.conter;

import org.apache.flink.api.common.JobExecutionResult;
import org.apache.flink.api.common.accumulators.LongCounter;
import org.apache.flink.api.common.functions.RichMapFunction;
import org.apache.flink.api.java.DataSet;
import org.apache.flink.api.java.ExecutionEnvironment;
import org.apache.flink.api.java.operators.DataSource;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.core.fs.FileSystem;

public class JavaCounterApp {
    public static void main(String[] args) throws Exception {
        ExecutionEnvironment env = ExecutionEnvironment.getExecutionEnvironment();
        DataSource<String> data = env.fromElements("holiday", "spark", "holiday");
        DataSet<String> info = data.map(new RichMapFunction<String, String>() {
            LongCounter counter = new LongCounter();

            @Override
            public void open(Configuration parameters) throws Exception {
                super.open(parameters);
                getRuntimeContext().addAccumulator("x", counter);
            }

            public String map(String value) throws Exception {
                counter.add(1);
                return value;
            }
        });
        String filePath = "data/counter/java";
        info.writeAsText(filePath, FileSystem.WriteMode.OVERWRITE);
        JobExecutionResult result = env.execute(Thread.currentThread().getStackTrace()[1].getClassName());
        Object x = result.getAccumulatorResult("x");
        System.out.println(x);
    }
}
