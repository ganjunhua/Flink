package com.holiday.flink.train.project

import java.text.SimpleDateFormat
import java.util.{Date, Properties}

import org.apache.kafka.clients.producer.{KafkaProducer, ProducerRecord}
import org.apache.kafka.common.serialization.StringSerializer

import scala.util.Random


object HolidayKafkaProducer {


  def main(args: Array[String]): Unit = {
    val properties = new Properties()
    properties.setProperty("bootstrap.servers", "holiday-3:9092")
    properties.setProperty("key.serializer", classOf[StringSerializer].getName)
    properties.setProperty("value.serializer", classOf[StringSerializer].getName)
    val producer = new KafkaProducer[String, String](properties)
    val topic = "flink"
    while (true) {
      val builder = new StringBuilder
      builder.append("holiday").append("\t")
        .append("CN").append("\t")
        .append(getLevels()).append("\t")
        .append(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date())).append("\t")
        .append(getIP()).append("\t")
        .append(getDomains()).append("\t")
        .append(getTraffic()).append("\t")
      println(builder.toString())
      producer.send(new ProducerRecord[String, String](topic, builder.toString()))
      Thread.sleep(2000)
    }
  }

  def getLevels(): String = {
    val levels = Array("M", "E")
    levels(new Random().nextInt(levels.length))
  }

  def getIP(): String = {
    val ips = Array("192.168.182.140",
      "192.168.182.141",
      "192.168.182.142",
      "192.168.182.143",
      "192.168.182.144",
      "192.168.182.145",
      "192.168.182.146")
    ips(new Random().nextInt(ips.length))
  }

  def getDomains(): String = {
    val domains = Array("http://holiday-1:8081/#/overview"
      , "http://holiday-1:50070/#/overview"
      , "http://holiday-1:8082/#/overview"
      , "http://holiday-1:8088/#/overview"
      , "http://holiday-1:19888/#/overview"
      , "http://holiday-1:9092/#/overview"
    )
    domains(new Random().nextInt(domains.length))
  }

  def getTraffic(): Long = {
    new Random().nextInt(10000)
  }
}
