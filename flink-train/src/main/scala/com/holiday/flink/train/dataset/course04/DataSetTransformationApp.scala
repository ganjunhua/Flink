package com.holiday.flink.train.dataset.course04

import org.apache.flink.api.common.operators.Order
import org.apache.flink.api.scala.ExecutionEnvironment
import org.apache.flink.api.scala._

import scala.collection.mutable.ListBuffer

object DataSetTransformationApp {
  def main(args: Array[String]): Unit = {
    val env = ExecutionEnvironment.getExecutionEnvironment
    crossFunction(env)
  }

  def crossFunction(env: ExecutionEnvironment): Unit = {
    //cross 笛卡尔集
    val info = List("曼联", "曼城")
    val info2 = List(3, 1, 0)
    val data1 = env.fromCollection(info)
    val data2 = env.fromCollection(info2)
    data1.cross(data2).print()
  }

  def outJoinFunction(env: ExecutionEnvironment): Unit = {

    //  编号---姓名
    val info1 = ListBuffer[(Int, String)]()
    info1.append((1, "h1"))
    info1.append((2, "h2"))
    info1.append((3, "h3"))
    info1.append((4, "h4"))
    // 编号---城市
    val info2 = ListBuffer[(Int, String)]()
    info2.append((1, "bj"))
    info2.append((2, "sh"))
    info2.append((3, "gz"))
    info2.append((5, "sz"))
    // join where(0) 0 为 data1，第一个Input, equalTo(0) 0 为第二个data2 Input,
    // 多个条件关系什么 逗号分隔
    val data1 = env.fromCollection(info1)
    val data2 = env.fromCollection(info2)
    //join结果为 ((3,h3),(3,gz))  ((1,h1),(1,bj))
    // 使用apply 处理 每个 无组  first =  (3,h3)  ，second =(3,gz)
    data1.leftOuterJoin(data2).where(0).equalTo(0).apply((first, second) => {
      if (second == null) (first._1, first._2, "-") else (first._1, first._2, second._2)
    }).print()
  }

  def joinFunction(env: ExecutionEnvironment): Unit = {
    //  编号---姓名
    val info1 = ListBuffer[(Int, String)]()
    info1.append((1, "h1"))
    info1.append((2, "h2"))
    info1.append((3, "h3"))
    info1.append((4, "h4"))
    // 编号---城市
    val info2 = ListBuffer[(Int, String)]()
    info2.append((1, "bj"))
    info2.append((2, "sh"))
    info2.append((3, "gz"))
    info2.append((5, "sz"))
    // join where(0) 0 为 data1，第一个Input, equalTo(0) 0 为第二个data2 Input,
    // 多个条件关系什么 逗号分隔
    val data1 = env.fromCollection(info1)
    val data2 = env.fromCollection(info2)
    //join结果为 ((3,h3),(3,gz))  ((1,h1),(1,bj))
    // 使用apply 处理 每个 无组  first =  (3,h3)  ，second =(3,gz)
    data1.join(data2).where(0).equalTo(0).apply((first, second) => {
      (first._1, first._2, second._2)
    }).print()
  }

  def distinctFunction(env: ExecutionEnvironment): Unit = {
    val info = ListBuffer[String]()
    info.append("hadoop,spark")
    info.append("hadoop,spark")
    info.append("hadoop.flink")
    val data = env.fromCollection(info)
    //去重
    data.flatMap(_.split(",")).distinct().print()
  }

  def flatMapFunction(env: ExecutionEnvironment): Unit = {
    val info = ListBuffer[String]()
    info.append("hadoop,spark")
    info.append("hadoop,flink")
    info.append("flink,flink")
    val data = env.fromCollection(info)
    //将data 数据打散，行转列,wordcount
    data.flatMap(_.split(",")).map((_, 1)).groupBy(0).sum(1).print()
  }

  def firstFunction(env: ExecutionEnvironment): Unit = {
    val info = ListBuffer[(Int, String)]()
    info.append((1, "hadoop"))
    info.append((1, "spark"))
    info.append((1, "flink"))
    info.append((2, "java"))
    info.append((2, "spring boot"))
    info.append((3, "linux"))
    info.append((4, "vue"))

    val data = env.fromCollection(info)
    // 取前三条数据
    data.first(3).print()
    //分组后取前两条数据，等于sql中 rownumber order by
    data.groupBy(0).first(2).print()
    //分组后取前两条数据，等于sql中 rownumber partition by order by
    data.groupBy(0).sortGroup(1, Order.DESCENDING).first(2).print()
  }

  def mapPartitionFunction(env: ExecutionEnvironment): Unit = {
    //dataSource 100个元素，把结果存储 到数据库中
    val students = new ListBuffer[String]
    for (i <- 1 to 100) {
      students.append("student: " + i)
    }
    val data = env.fromCollection(students).setParallelism(4)
    //mapPartition 每次处理一个分区 的数据，有多少分区，就会处理多少次
    // x.map 这个是处理每个分区具体里面的值
    data.mapPartition(x => x.map(x => {
      (x + "xz")
    })).print()
  }

  def filterFunction(env: ExecutionEnvironment): Unit = {
    val data = env.fromCollection(List(1 to 10: _*))
    data.map(_ + 1).filter(_ > 3).print()
  }

  def mapFunction(env: ExecutionEnvironment): Unit = {
    val data = env.fromCollection(List(1 to 10: _*))
    data.map(_.toInt).print()
  }
}
