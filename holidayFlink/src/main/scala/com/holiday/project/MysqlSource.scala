package com.holiday.project

import java.sql.{Connection, DriverManager, PreparedStatement}

import org.apache.flink.configuration.Configuration
import org.apache.flink.streaming.api.functions.source.{RichParallelSourceFunction, SourceFunction}

import scala.collection.mutable

class MysqlSource extends RichParallelSourceFunction[mutable.HashMap[String, String]] {
  override def cancel() = {

  }

  var connection: Connection = null
  var ps: PreparedStatement = null

  // 创建数据连接，只执行一次
  override def open(parameters: Configuration): Unit = {
    val driver = "com.mysql.jdbc.Driver"
    Class.forName(driver)
    val url = "jdbc:mysql://localhost:3306/holiday"
    val user = "root"
    val password = "admin123"
    connection = DriverManager.getConnection(url, user, password)
    val sql = "select user_id,domain from userdomain"
    ps = connection.prepareStatement(sql)
  }

  //释放资源
  override def close(): Unit = {
    if (ps != null) {

      ps.close()
    }
    if (connection != null) {

      connection.close()
    }
  }

  /**
    * 要从mysql表中把数据读取出来转成map进行数据的封装
    *
    * @param ctx
    */
  override def run(ctx: SourceFunction.SourceContext[mutable.HashMap[String, String]]) = {

    val result = ps.executeQuery()
    val dataMap = new mutable.HashMap[String, String]()
    while (result.next()) {
      val userid = result.getString("user_id")
      val domain = result.getString("domain")
      if (!(domain.isEmpty && userid.isEmpty)) {
        dataMap.put(domain, userid)
      }

    }
    ctx.collect(dataMap)
  }
}
