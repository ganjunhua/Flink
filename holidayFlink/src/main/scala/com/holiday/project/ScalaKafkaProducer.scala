package com.holiday.project

import java.text.SimpleDateFormat
import java.util.{Date, Properties}

import org.apache.kafka.clients.producer.{KafkaProducer, ProducerRecord}
import org.apache.kafka.common.serialization.StringSerializer

import scala.collection.mutable.ArrayBuffer
import scala.util.Random

object ScalaKafkaProducer {
  def main(args: Array[String]): Unit = {
    val properties = getProperties()
    val produce = new KafkaProducer[String, String](properties)
    val topic = "test"
    while (true) {
      val builder = new StringBuilder
      builder.append("scala").append("\t")
      builder.append("CN").append("\t")
      builder.append(getLevels).append("\t")
      builder.append(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date)).append("\t")
      builder.append(getIps).append("\t")
      builder.append(getDomains).append("\t")
      builder.append(getTraffic).append("\t")
      println(builder.toString())
      produce.send(new ProducerRecord[String, String](topic, builder.toString()))
      Thread.sleep(2000)
    }
  }

  def getProperties(): Properties = {
    val properties = new Properties()
    properties.setProperty("bootstrap.servers", "192.168.226.128:9092")
    properties.setProperty("key.serializer", classOf[StringSerializer].getName)
    properties.setProperty("value.serializer", classOf[StringSerializer].getName)
    properties
  }

  def getTraffic(): Int = {
    Random.nextInt(10000)
  }

  def getDomains(): String = {
    val domains = new ArrayBuffer[String]()
    domains += "www.holiday.com"
    domains += "www.spark.com"
    domains += "www.flink.com"
    domains += "www.mysql.com"
    domains += "www.oarcle.com"
    domains += "www.scala.com"
    domains(new Random().nextInt(domains.length))
  }

  def getLevels(): String = {
    var levels = new ArrayBuffer[String]()
    levels += "M"
    levels += "E"
    levels(new Random().nextInt(levels.length))
  }

  def getIps(): String = {
    val ipList = new ArrayBuffer[String]()
    ipList += "127.0.0.1"
    ipList += "127.0.0.2"
    ipList += "127.0.0.3"
    ipList += "127.0.0.4"
    ipList += "127.0.0.5"
    ipList += "127.0.0.6"
    ipList += "127.0.0.7"
    ipList(new Random().nextInt(ipList.length - 1))
  }
}
