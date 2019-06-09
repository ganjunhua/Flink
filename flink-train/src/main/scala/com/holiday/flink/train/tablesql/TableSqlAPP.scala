package com.holiday.flink.train.tablesql

import org.apache.flink.api.scala.{DataSet, ExecutionEnvironment, _}
import org.apache.flink.table.api.TableEnvironment
import org.apache.flink.types.Row

object TableSqlAPP {

  case class SalesLog(transactionid: String
                      , custmerid: String
                      , itemid: String
                      , amountpaid: Int
                     )

  //使用批处理 table
  def main(args: Array[String]): Unit = {
    //创建 批处理执行环境
    val env = ExecutionEnvironment.getExecutionEnvironment
    // 使用批处理创建 table 对象
    val tableEnv = TableEnvironment.getTableEnvironment(env)
    val filePath = "data/tablesql/sales.csv"
    // 读取数据
    val csv = env.readCsvFile[SalesLog](filePath, ignoreFirstLine = true)
    //  由 DataSet ==》table,返回一个table
    val salesTable = tableEnv.fromDataSet(csv)
    //由 table类型创建一张表
    tableEnv.registerTable("sales", salesTable)
    //进行sql查询
    val resultTable = tableEnv.sqlQuery("select custmerid,sum(amountpaid) money from sales group by custmerid")
    //结果输出
    resultTable.printSchema()
    tableEnv.toDataSet[Row](resultTable).print()
  }
}
