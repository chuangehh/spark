package com.atlcc

import org.apache.spark.{SparkConf, SparkContext}

/**
  * 统计每一个省份点击TOP3的广告ID
  *
  * @author liangchuanchuan
  */
object ProAndAd2Agent {

  def main(args: Array[String]): Unit = {

    // conf
    val conf = new SparkConf().setAppName("proAndAd2Agent").setMaster("local[*]")

    // sparkContext
    val sc = new SparkContext(conf)

    // load file
    val rdds = sc.textFile("agent.log")

    // 1516609143867 6 7 64 16
    // 格式 ：timestamp province city userid adid
    // 某个时间点 某个省份 某个城市 某个用户 某个广告
    // 转换k->v,省份_广告:数量  pro_ad -> 1
    val proAndAd2KeyValue = rdds.map {
      (item) =>
        val splits = item.split(" ")
        (splits(1) + "_" + splits(4), 1)
    }

    // 聚合数据 pro_ad -> 100
    // 粒度转换 省份  pro -> (100,ad)
    val proKeyValue = proAndAd2KeyValue.reduceByKey(_ + _).map {
      (item) =>
        val splits = item._1.split("_")
        (splits(0), (item._2, splits(1)))
    }

    // 根据省份分组 pro -> Array((100,ad),(100,ad))
    proKeyValue.groupByKey().mapValues {
      (values) =>
        values.toList.sortWith((kv1, kv2) => kv1._1 > kv2._1).take(3)
    }.saveAsTextFile("proAndAd2Result")

    // close sparkContext
    sc.stop()
  }

}

