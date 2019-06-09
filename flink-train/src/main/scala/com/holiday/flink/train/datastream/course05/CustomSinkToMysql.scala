package com.holiday.flink.train.datastream.course05


import org.apache.flink.streaming.api.scala.StreamExecutionEnvironment
import org.apache.flink.api.scala._
import org.apache.flink.streaming.api.TimeCharacteristic

object CustomSinkToMysql {


  def main(args: Array[String]): Unit = {
    val env = StreamExecutionEnvironment.getExecutionEnvironment
    val source = env.socketTextStream("holiday-7-1", 8888)
    env.setStreamTimeCharacteristic(TimeCharacteristic.EventTime)
    val data = source.map(x => x.split(",")).map(x => {
      val stu: Student = new Student()
      stu.id = x(0).toInt
      stu.name = x(1)
      stu.age = x(2).toInt
      println("stu :" + stu)
      stu
    })
    data.addSink(new SinkTOMySQScala())

    env.execute(this.getClass.getSimpleName)

  }
}
