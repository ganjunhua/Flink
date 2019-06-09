package com.holiday.flink.train.datastream.course05

class Student {
  var id: Int = 0;
  var name: String = null
  var age: Int = 0

  override def toString = s"Student(id=$id, name=$name, age=$age)"
}
