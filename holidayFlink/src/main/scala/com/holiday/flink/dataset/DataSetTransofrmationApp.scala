package com.holiday.flink.dataset

import org.apache.flink.api.common.operators.Order
import org.apache.flink.api.scala.ExecutionEnvironment
import org.apache.flink.api.scala._

import scala.collection.mutable.ListBuffer

object DataSetTransofrmationApp {
  def main(args: Array[String]): Unit = {
    val env = ExecutionEnvironment.getExecutionEnvironment
    OutjoinFunction(env)
  }

  def OutjoinFunction(env: ExecutionEnvironment): Unit = {
    val info1 = ListBuffer[(Int, String)]()
    info1.append((1, "holiday1"))
    info1.append((2, "holiday2"))
    info1.append((3, "aoliday3"))
    info1.append((4, "aoliday4"))

    val info2 = ListBuffer[(Int, String)]()
    info2.append((1, "北京"))
    info2.append((2, "上海"))
    info2.append((3, "广州"))
    info2.append((5, "深圳"))
    val data1 = env.fromCollection(info1)
    val data2 = env.fromCollection(info2)
    val data3 = data1.leftOuterJoin(data2).where(0).equalTo(0)
    data3.apply((x1, x2) => {
      if (x2 == null) {
        (x1._1, x1._2, "---")
      } else (x1._1, x1._2, x2._2)
    }).print()
  }

  def joinFunction(env: ExecutionEnvironment): Unit = {
    val info1 = ListBuffer[(Int, String)]()
    info1.append((1, "holiday1"))
    info1.append((2, "holiday2"))
    info1.append((3, "aoliday3"))
    info1.append((4, "aoliday4"))

    val info2 = ListBuffer[(Int, String)]()
    info2.append((1, "北京"))
    info2.append((2, "上海"))
    info2.append((3, "广州"))
    info2.append((5, "深圳"))
    val data1 = env.fromCollection(info1)
    val data2 = env.fromCollection(info2)
    val data3 = data1.join(data2).where(0).equalTo(0)
    data3.map(x => {
      (x._1._1, x._1._2, x._2._2)
    }).print()
  }

  def distinctFunction(env: ExecutionEnvironment): Unit = {
    val info = ListBuffer[String]()
    info.append("holiday,spark")
    info.append("flink,spark")
    info.append("scala,spark")
    val data = env.fromCollection(info)
    data.flatMap(x => x.split(",")).distinct().print()
  }

  def flatMapFunction(env: ExecutionEnvironment): Unit = {
    val info = ListBuffer[String]()
    info.append("holiday,spark")
    info.append("flink,spark")
    info.append("scala,spark")
    val data = env.fromCollection(info)
    data.flatMap(x => x.split(",")).map((_, 1)).groupBy(0).sum(1).print()
  }

  def firstFunction(env: ExecutionEnvironment): Unit = {
    val info = ListBuffer[(Int, String)]()
    info.append((1, "holiday1"))
    info.append((2, "holiday2"))
    info.append((2, "aoliday2"))
    info.append((3, "holiday3"))
    info.append((3, "aoliday3"))
    info.append((4, "holiday4"))
    info.append((5, "holiday5"))
    val data = env.fromCollection(info)
    data.groupBy(0).sortGroup(1, Order.DESCENDING).first(1).print()
  }

  def mapPartitionFunction(env: ExecutionEnvironment): Unit = {
    val data = new ListBuffer[String]
    for (i <- 1 to 100) {
      data.append("data:" + i)
    }
    val data1 = env.fromCollection(data)
    val a = data1.mapPartition(x => {
      val connection = DBUtils.getConection()
      println(connection + "......")
      DBUtils.returnConnection(connection)
      x
    })

    println(a.print())


  }

  def filterFunction(env: ExecutionEnvironment): Unit = {
    val data = env.fromCollection(List(1, 2, 3, 4, 5))
    data.map(_ + 1).filter(_ > 4).print()
  }

  def mapFunction(env: ExecutionEnvironment): Unit = {
    val data = env.fromCollection(List(1, 2, 3, 4, 5))
    //对data中的每个元素都做一个+1的操作
    data.map(_ + 1).print()

  }
}
