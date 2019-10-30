package DataStream.flink.Socket

import java.sql.DriverManager

import org.apache.flink.configuration.Configuration
import org.apache.flink.streaming.api.functions.sink.{RichSinkFunction, SinkFunction}

class MysqlSink extends RichSinkFunction[Student] {
  override def open(parameters: Configuration): Unit = {
    print("open" + "调用方法")
    super.open(parameters)
    Class.forName(MysqlPro.driverName)
    MysqlPro.conn = DriverManager.getConnection(MysqlPro.jdbcUrl, MysqlPro.username, MysqlPro.password)
    MysqlPro.conn.setAutoCommit(false)
  }

  override def invoke(value: Student, context: SinkFunction.Context[_]): Unit = {
    print("invoke" + value)
    val xx = "?,?"
    MysqlPro.ps = MysqlPro.conn.prepareStatement(s"insert into student(age,name) values($xx)")
    MysqlPro.ps.setInt(1, value.age)
    MysqlPro.ps.setString(2, value.name)
    MysqlPro.ps.execute()
    MysqlPro.conn.commit()
  }

  override def close(): Unit = {
    if (MysqlPro.conn != null) {
      MysqlPro.conn.commit()
      MysqlPro.conn.close()
    }
  }
}
