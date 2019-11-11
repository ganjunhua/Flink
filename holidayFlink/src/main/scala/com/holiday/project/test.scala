package com.holiday.project

import java.text.SimpleDateFormat
import java.util.Date
object test {
  def main(args: Array[String]): Unit = {

    val a = new SimpleDateFormat("yyyy-MM-dd HH:mm").format(new Date("2019-11-08 16:33:19"))
    println(a)

  }
}
