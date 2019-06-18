package com.holiday.flink.train.project


import java.sql.{Connection, DriverManager, PreparedStatement}

import org.apache.flink.configuration.Configuration
import org.apache.flink.streaming.api.functions.source.{RichParallelSourceFunction, SourceFunction}

import scala.collection.mutable

class HolidayMysql extends RichParallelSourceFunction[mutable.HashMap[String, String]] {

  var connection: Connection = null
  var ps: PreparedStatement = null
  var isRunning = true

  //用来建立连接
  override def open(parameters: Configuration): Unit = {
    val driver = "com.mysql.jdbc.Driver"
    val url = "jdbc:mysql://localhost:3306/holiday"
    val user = "root"
    val password = "admin123"
    Class.forName(driver)
    connection = DriverManager.getConnection(url, user, password)
    val sql = "select user_id,domain from userdomain"
    ps = connection.prepareStatement(sql)
  }

  //释放资源
  override def cancel() = {
    if (connection != null) connection.close()
    if (ps != null) ps.close()
    isRunning = false
  }

  /**
    * 此处是代码的关键：要从mysql表中把数据读取转成map进行数据的封装
    *
    * @param ctx
    */
  override def run(ctx: SourceFunction.SourceContext[mutable.HashMap[String, String]]) = {
    // ctx.collect()
    val dataMap = new mutable.HashMap[String, String]()

      val result = ps.executeQuery()
      while (result.next()) {
        val domain = result.getString("domain")
        val userid = result.getString("user_id")
        if (!(domain.isEmpty && userid.isEmpty)) {
          dataMap.put(domain, userid)
      }
      ctx.collect(dataMap)
    }
  }

}
