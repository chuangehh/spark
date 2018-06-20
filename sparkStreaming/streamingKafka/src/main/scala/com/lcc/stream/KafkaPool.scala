package com.lcc.stream

import java.util.Properties

import org.apache.commons.pool2.BasePooledObjectFactory
import org.apache.commons.pool2.impl.{DefaultPooledObject, GenericObjectPool}
import org.apache.kafka.clients.producer.{KafkaProducer, ProducerConfig, ProducerRecord}

/**
  * kafka 代理
  *
  * @param brokers kafka cluster host
  * @author liangchuanchuan
  */
class KafkaProxy(brokers: String) {

  private val pros: Properties = new Properties()
  pros.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, brokers)
  pros.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringSerializer")
  pros.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringSerializer")

  /**
    * kafka 生产者
    */
  val kafkaProducer = new KafkaProducer[String, String](pros)

  def send(topic: String, key: String, value: String): Unit = {
    kafkaProducer.send(new ProducerRecord(topic, key, value))
  }

  def send(topic: String, value: String): Unit = {
    kafkaProducer.send(new ProducerRecord(topic, value))
  }

}

/**
  * kafka生产者连接池代理工厂
  *
  * @param brokers
  */
class KafkaProxyFactory(brokers: String) extends BasePooledObjectFactory[KafkaProxy] {

  /**
    * 创建一个需要代理的对象
    *
    * @return
    */
  override def create() = new KafkaProxy(brokers)

  /**
    * 包装一个pool 对象
    *
    * @param obj
    * @return
    */
  override def wrap(obj: KafkaProxy) = new DefaultPooledObject(obj)
}

/**
  * kafka连接池对象,每个Executor 默认8个线程
  */
object KafkaPool {

  private var factory: GenericObjectPool[KafkaProxy] = null

  def apply(brokers: String) = {
    if (factory == null) {
      KafkaPool.synchronized {
        if (factory == null) {
          factory = new GenericObjectPool(new KafkaProxyFactory(brokers))
        }
      }
    }
    factory
  }

}
