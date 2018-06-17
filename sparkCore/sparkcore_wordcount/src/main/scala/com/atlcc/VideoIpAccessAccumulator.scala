package com.atlcc


import java.util
import java.util.concurrent.ConcurrentHashMap

import org.apache.spark.util.AccumulatorV2


/**
  * 视频ip 访问累加器
  *
  * @author liangchuanchuan
  */
class VideoIpAccessAccumulator extends AccumulatorV2[(String, String), java.util.Map[String, java.util.Set[String]]] {

  private val _map = new ConcurrentHashMap[String, java.util.Set[String]]

  override def isZero: Boolean = _map.isEmpty

  override def copy(): AccumulatorV2[(String, String), util.Map[String, util.Set[String]]] = {
    val newAcc = new VideoIpAccessAccumulator
    _map.synchronized {
      newAcc._map.putAll(_map)
    }
    newAcc
  }

  override def reset(): Unit = _map.clear()

  override def add(v: (String, String)): Unit = {
    var ipSet = _map.get(v._1)
    if (_map.get(v._1) == null) {
      ipSet = new util.HashSet[String]()
      _map.put(v._1, ipSet)
    }
    ipSet.add(v._2)
  }

  override def merge(other: AccumulatorV2[(String, String), util.Map[String, util.Set[String]]]): Unit = other match {
    case o: VideoIpAccessAccumulator => _map.putAll(o.value)
    case _ => throw new UnsupportedOperationException(
      s"Cannot merge ${this.getClass.getName} with ${other.getClass.getName}")
  }

  override def value: util.Map[String, util.Set[String]] = _map.synchronized {
    java.util.Collections.unmodifiableMap(new ConcurrentHashMap[String, java.util.Set[String]](_map))
  }
}
