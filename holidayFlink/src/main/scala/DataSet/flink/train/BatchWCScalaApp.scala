package DataSet.flink.train

import org.apache.flink.api.scala.{ExecutionEnvironment, _}

object BatchWCScalaApp {
  def main(args: Array[String]): Unit = {
    val input = "data/wc"
    //1、得到上下文
    val env = ExecutionEnvironment.getExecutionEnvironment
    // 2、读取数据 获取的数据行的顺序是乱序的
    val text = env.readTextFile(input)
    //3、转换操作，scala必须导入隐式转换
    val words = text.flatMap(x => x.split(",")).filter(x => x.nonEmpty)
      .map(x => (x, 1))
      .groupBy(0)
      .sum(1)
    //4、将结果写入存储
    words.print()

  }
}
