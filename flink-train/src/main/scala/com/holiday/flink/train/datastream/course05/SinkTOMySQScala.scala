package com.holiday.flink.train.datastream.course05

import java.sql.{Connection, DriverManager, PreparedStatement}

import org.apache.flink.configuration.Configuration
import org.apache.flink.streaming.api.functions.sink.{RichSinkFunction, SinkFunction}


class SinkTOMySQScala extends RichSinkFunction[Student] {
  var connection: Connection = null;
  var pstmt: PreparedStatement = null;

  def getConnection(): Connection = {

    var conn: Connection = null;
    try {
      Class.forName("com.mysql.jdbc.Driver")
      val url = "jdbc:mysql://localhost:3306/holiday"
      conn = DriverManager.getConnection(url, "root", "admin123")
    } catch {
      case e: Exception => println(e)
    }
    conn
  }

  override def open(parameters: Configuration): Unit = {
    super.open(parameters)
    connection = getConnection()
    val sql = "insert into flink_student(id,name,age) values (?,?,?)"
    pstmt = connection.prepareStatement(sql)
  }


  override def invoke(value: Student, context: SinkFunction.Context[_]): Unit = {
   // super.invoke(value, context)
    pstmt.setInt(1, value.id)
    pstmt.setString(2, value.name)
    pstmt.setInt(3, value.age)
    pstmt.executeUpdate()
  }

  override def close(): Unit = {
    super.close()
    pstmt.close()
    connection.close()
  }

}
