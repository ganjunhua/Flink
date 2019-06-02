package com.holiday.flink.train.dataset.course04

import scala.util.Random

object DBUtils {
  def getConection() = {
    new Random().nextInt(10) + ""

  }

  def returnConnection(connection: String): Unit = {

  }
}
