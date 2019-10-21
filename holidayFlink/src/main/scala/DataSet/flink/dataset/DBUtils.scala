package DataSet.flink.dataset

import scala.util.Random

object DBUtils {
  def getConection() = {
    new Random().nextInt(10) + ""
  }

  def returnConnection(connection: String): Unit = {

  }
}
