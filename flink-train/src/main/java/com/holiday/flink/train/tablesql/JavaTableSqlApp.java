package com.holiday.flink.train.tablesql;

import org.apache.flink.api.java.DataSet;
import org.apache.flink.api.java.ExecutionEnvironment;
import org.apache.flink.table.api.BatchTableEnvironment;
import org.apache.flink.table.api.Table;

public class JavaTableSqlApp {
    public static void main(String[] args) throws Exception {
        // 创建批处理环境
        ExecutionEnvironment env = ExecutionEnvironment.getExecutionEnvironment();
        //使用批处理 创建 table执行环境
        BatchTableEnvironment tableEnv = BatchTableEnvironment.getTableEnvironment(env);
        //读取数据
        String filePath = "data/tablesql/sales.csv";
        DataSet<Sales> csv = env.readCsvFile(filePath)
                .ignoreFirstLine()
                .pojoType(Sales.class, "transactionid", "customerid", "itemid", "amoutpaid");
       // Table sales = tableEnv.fromDataSet(csv);
    }

    public static class Sales {
        public String transactionid;
        public String customerid;
        public String itemid;
        public double amoutpaid;

        @Override
        public String toString() {
            return "Sales{" +
                    "transactionid='" + transactionid + '\'' +
                    ", customerid='" + customerid + '\'' +
                    ", itemid='" + itemid + '\'' +
                    ", amoutpaid=" + amoutpaid +
                    '}';
        }
    }
}
