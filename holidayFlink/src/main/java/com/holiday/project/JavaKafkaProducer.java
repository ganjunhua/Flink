package com.holiday.project;


import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.StringSerializer;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Properties;
import java.util.Random;

public class JavaKafkaProducer {
    public static void main(String[] args) throws Exception {
        // 创建生产者信息   控制台消费 ./kafka-console-consumer.sh --bootstrap-server holiday-1:9092 --topic test
        Properties properties = new Properties();
        properties.setProperty("bootstrap.servers", "192.168.226.128:9092");
        properties.setProperty("key.serializer", StringSerializer.class.getName());
        properties.setProperty("value.serializer", StringSerializer.class.getName());

        // 创建生产者
        KafkaProducer<String, String> producer = new KafkaProducer<String, String>(properties);
        String topic = "test";
        //通过死循环一直不停往kafka的broker里面生产数据
        while (true) {
            StringBuilder builder = new StringBuilder();
            builder.append("java").append("\t");
            builder.append("CN").append("\t");
            builder.append(getLevels()).append("\t");
            //获取当前时间
            builder.append(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date())).append("\t");
            builder.append(getIps()).append("\t");
            builder.append(getDomains()).append("\t");
            builder.append(getTraffic()).append("\t");
            System.out.println(builder.toString());
            producer.send(new ProducerRecord<String, String>(topic, builder.toString()));
            Thread.sleep(2000);
        }

    }

    private static int getTraffic() {

        return new Random().nextInt(10000);
    }

    private static String getDomains() {
        String[] domains = new String[]{
                "www.holiday.com",
                "www.spark.com",
                "www.flink.com",
                "www.mysql.com",
                "www.oarcle.com",
                "www.scala.com",
                "www.java.com"
        };
        return domains[new Random().nextInt(domains.length)];
    }

    //获取ip
    private static String getIps() {
        String ips[] = new String[]{
                "127.0.0.1",
                "127.0.0.2",
                "127.0.0.3",
                "127.0.0.4",
                "127.0.0.5",
                "127.0.0.6",
                "127.0.0.7",
                "127.0.0.8"
        };
        return ips[new Random().nextInt(ips.length)];
    }

    //  生产level数据
    public static String getLevels() {
        String[] levels = new String[]{"M", "E"};
        return levels[new Random().nextInt(levels.length)];
    }
}
