package com.atlcc

import java.text.SimpleDateFormat

import org.apache.spark.{SparkConf, SparkContext}
import org.joda.time.DateTime
import org.joda.time.format.DateTimeFormat

/**
  * 统计每一个省份每一个小时的TOP3广告的ID
  *
  * 1516609143867 6 7 64 16
  * 格式 ：timestamp province city userid adid
  * 某个时间点 某个省份 某个城市 某个用户 某个广告
  *
  * @author liangchuanchuan
  */
object ProAndAdAndHour3Agent {

  /**
    * 获取小时
    *
    * @param dataTime
    * @return
    */
  def getHour(dataTime: String): String = {
    val dateTime = new DateTime(dataTime.toLong)
    dateTime.toString("yyyyMMddHH")
  }

  def main(args: Array[String]): Unit = {

    // conf
    val conf = new SparkConf().setAppName("proAndAd2Agent").setMaster("local[*]")

    // sparkContext
    val sc = new SparkContext(conf)

    // load file RDD[String]
    val logs = sc.textFile("agent.log")

    // 转换k->v,省份_广告:数量 RDD[ Array[pro_hour_ad:1] ]
    val pro_hour_ad2Num = logs.map {
      (item) =>
        val splits = item.split(" ")
        (s"${splits(1)}_${splits(0)}_${splits(4)}", 1)
    }

    // 聚合数据 RDD[ Array[pro_hour_ad:sum] ]
    val pro_hour_ad2Sum = pro_hour_ad2Num.reduceByKey(_ + _)
    // 粒度转换省份小时 RDD[ Array[pro_hour:(ad,sum)] ]
    val pro_hour2AdSum = pro_hour_ad2Sum.map { (item) =>
      val splits = item._1.split("_")
      (splits(0), (item._2, splits(1)))
    }

    // 根据省份分组 pro -> Array((100,ad),(100,ad))
//    proKeyValue.groupByKey().mapValues {
//      (values) =>
//        values.toList.sortWith((kv1, kv2) => kv1._1 > kv2._1).take(3)
//    }.saveAsTextFile("proAndAdAndHour3Agent")

    // close sparkContext
    sc.stop()
  }
}
