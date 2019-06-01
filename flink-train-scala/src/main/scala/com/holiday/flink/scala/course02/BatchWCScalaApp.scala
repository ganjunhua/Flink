package com.holiday.flink.scala.course02

import org.apache.flink.api.scala.ExecutionEnvironment
import org.apache.flink.api.scala._
object BatchWCScalaApp {
  def main(args: Array[String]): Unit = {
    val input = "data/wordCount"
    val env = ExecutionEnvironment.getExecutionEnvironment
    val text = env.readTextFile(input)
    text.flatMap(x => x.split(" "))
      .map(x => (x, 1))
      .groupBy(0)
      .sum(1)
      .print()
  }
}
