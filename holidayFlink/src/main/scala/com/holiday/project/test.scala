package com.holiday.project

import scala.collection.mutable.ArrayBuffer

object test {
  def main(args: Array[String]): Unit = {
    val arr1: ArrayBuffer[String] = new ArrayBuffer[String]()
    arr1(1) = "hadoop"
    arr1(2) = "sss"

    println(arr1(1))
  }
}
