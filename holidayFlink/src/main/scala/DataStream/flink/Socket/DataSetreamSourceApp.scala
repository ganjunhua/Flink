package DataStream.flink.Socket

import DataStream.flink.customdatasource.CustomNonParalleleSource
import org.apache.flink.streaming.api.scala.StreamExecutionEnvironment
import org.apache.flink.api.scala._

object DataSetreamSourceApp {
  def main(args: Array[String]): Unit = {
    val env = StreamExecutionEnvironment.getExecutionEnvironment

    SocketSinkToMysql(env)

    env.execute("DataSetreamSourceApp")
  }

  def SocketSinkToMysql(env: StreamExecutionEnvironment): Unit = {
    val data = env.socketTextStream("192.168.226.128", 9999)
    data.map(x => x.split(",")).map(x => {
      println("xxxxxxxxxSocketSinkToMysqlxx" + x)
      val stu: Student = new Student()
      stu.age = x(0).toInt
      stu.name = x(1)
      stu
    }).addSink(new MysqlSink)
  }

  def NonCustomNonParalleleSource(env: StreamExecutionEnvironment): Unit = {
    val data = env.addSource(new CustomNonParalleleSource)
    data.print()


  }

  def socketFunction(env: StreamExecutionEnvironment): Unit = {
    val data = env.socketTextStream("192.168.226.128", 9999)
    data.print()
  }
}
