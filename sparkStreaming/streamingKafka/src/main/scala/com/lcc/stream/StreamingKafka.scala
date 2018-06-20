package com.lcc.stream

import kafka.serializer.StringDecoder
import org.apache.kafka.clients.consumer.ConsumerConfig
import org.apache.spark.SparkConf
import org.apache.spark.streaming.kafka.KafkaUtils
import org.apache.spark.streaming.{Seconds, StreamingContext}

/**
  * kafka streaming
  *
  * @author liangchuanchuan
  */
object StreamingKafka {

  // kafka cluster
  val brokerList = "hadoop101:9092,hadoop102:9092,hadoop103:9092"
  // zookeeper cluster
  val zookeeper = "hadoop101:2181,hadoop102:2181,hadoop103:2181"

  // streaming consumer
  val sourceTopic = "spark_stream_source"
  // streaming product
  val tagetTopic = "spark_stream_target"
  val groupid = "consumer001"

  def getKafkaConf() = {
    //创建连接kafka的参数
    Map[String, String](
      ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG -> brokerList,
      ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG -> "org.apache.kafka.common.serialization.StringDeserializer",
      ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG -> "org.apache.kafka.common.serialization.StringDeserializer",
      ConsumerConfig.GROUP_ID_CONFIG -> groupid,
      ConsumerConfig.AUTO_OFFSET_RESET_CONFIG -> "largest"
    )
  }

  def main(args: Array[String]): Unit = {
    // 1.connection kafka
    println("直接链接kafka")
    val conf = new SparkConf().setMaster("local[*]").setAppName("StreamingKafka")
    val ssc = new StreamingContext(conf, Seconds(5))
    val textKafkaDStream = KafkaUtils.createDirectStream[String, String, StringDecoder, StringDecoder](ssc, getKafkaConf(), Set(sourceTopic))

    // 2.business option
    val kvDStream = textKafkaDStream.map(kv => (s"key: ${kv._1}", s"value: ${kv._2}"))

    // one DStream -> more RDD
    kvDStream.foreachRDD { rdd =>
      // one rdd -> more partition
      rdd.foreachPartition { items =>
        // 3.get kafka pool connection
        val pool = KafkaPool(brokerList)
        val producerConnection = pool.borrowObject()

        // 4.write business data to kafka
        for (item <- items) {
          producerConnection.send(tagetTopic, item._1, item._2)
        }

        // 5.return kafka pool connection
        pool.returnObject(producerConnection)
      }
    }

    ssc.start()
    ssc.awaitTermination()
  }

}