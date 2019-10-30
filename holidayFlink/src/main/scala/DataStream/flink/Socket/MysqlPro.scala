package DataStream.flink.Socket

import java.sql.{Connection, PreparedStatement}

object MysqlPro {
  var conn: Connection = _
  var ps: PreparedStatement = _
  val jdbcUrl = "jdbc:mysql://localhost:3306/holiday"
  val username = "root"
  val password = "admin123"
  val driverName = "com.mysql.jdbc.Driver"
}
