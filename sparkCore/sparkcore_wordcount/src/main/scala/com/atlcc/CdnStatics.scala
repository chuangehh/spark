package com.atlcc

import java.util.Locale

import org.apache.spark.rdd.RDD
import org.apache.spark.{SparkConf, SparkContext}
import org.joda.time.DateTime
import org.joda.time.format.DateTimeFormat

/**
  * CDN 统计
  *
  * 1、计算每一个IP的访问次数
  * 2、计算每一个视频访问的IP数
  * 3、统计每小时CDN的流量
  *
  * @author liangchuanchuan
  */
object CdnStatics {

  def main(args: Array[String]): Unit = {
//    val conf = new SparkConf().setAppName("proAndAd2Agent").setMaster("local[*]")
//    val sc = new SparkContext(conf)
//    val logs = sc.textFile("cdn.txt")
//

    //计算每一个IP的访问次数
    val conf = new SparkConf().setAppName("pageViews").setMaster("local[*]")

    val ct = new SparkContext(conf)

    val rowArray = ct.textFile("./cdn.txt").map(_.split(" "))

    val tuples = rowArray.filter(_ (1).endsWith("mp4"))
      .map(x => (x(6) + "_" + x(0), 1))
      .groupBy(x => x._1)
      .map(x => (x._1.split("_")(0), 1))
      .reduceByKey(_ + _).collect()


    //
//
//
//    //    hourFlow(logs)
//    videoIpAccessUseAcc(logs, sc)
//
//    sc.stop()
  }

  /**
    * 1.计算每一个IP的访问次数
    *
    * @param logs
    */
  def ipAccept(logs: RDD[String]): Unit = {
    //key:value ip:1
    logs.map { log =>
      val words = log.split(" ")
      (words(0), 1)
    }.reduceByKey(_ + _).saveAsTextFile("计算每一个IP的访问次数")
  }

  /**
    * 计算每一个视频访问的IP数
    *
    * @param logs
    */
  def videoIpAccess(logs: RDD[String]): Unit = {
    logs.filter(log =>
      log.contains(".mp4")
    )
      //    key,value video_ip:1
      .map { log =>
      val words = log.split(" ")
      val pattern = "[0-9]+.mp4".r
      val mp4 = pattern.findFirstIn(log)
      (mp4.get + "_" + words(0), 1)
    }
      .groupByKey()
      .map { item => (item._1.split("_")(0), 1) }
      .reduceByKey(_ + _)
      .saveAsTextFile("计算每一个视频访问的IP数")
  }

  /**
    * 计算每一个视频访问的IP数 使用累加器
    *
    * 数据结构: Map<video,Set<ip>>
    * 累加器原理: 每一个Host在累加器中放入数据,最后merage到results中
    *
    * #####
    * # A # 、
    * #####  、
    *
    * #####     、  ###########
    * # B # - - - > # results #
    * #####      /  ###########
    *
    * #####  /
    * # C #/
    * #####
    *
    * @param logs
    */
  def videoIpAccessUseAcc(logs: RDD[String], sc: SparkContext): Unit = {
    //注册累加器
    val collectAcc = new VideoIpAccessAccumulator
    sc.register(collectAcc, "collectAcc")

    //往累加器中放入数据
    val aa = logs.filter { log =>
      val pattern = "[0-9]+.mp4".r
      val ip = log.split(" ")(0)
      val video = pattern.findFirstIn(log)

      video match {
        case Some(_) => collectAcc.add((video.get, ip))
      }
      false
    }

    //获取累加器中的数据
    println(collectAcc.value)
  }

  /**
    * 3.统计每小时CDN的流量
    *
    * @param logs
    */
  def hourFlow(logs: RDD[String]): Unit = {
    //key:value hour:1
    logs.map { log =>
      // 时间  流量
      val flow_pattern = "([0-9]+) \"".r
      val datetime_parrern = "\\[[0-9]+/[\\S]+ \\+[0-9]+\\]".r

      var datetime = datetime_parrern.findFirstIn(log).get
      datetime = datetime.substring(1, datetime.length - 1)
      datetime = getHour(datetime)
      var flow = flow_pattern.findFirstIn(log).get
      flow = "[0-9]+".r.findFirstIn(flow).get
      if ("0034021506".eq(datetime)) {
        println(log)
      }

      (datetime, flow.toInt)
    }.reduceByKey(_ + _).saveAsTextFile("统计每小时CDN的流量")
  }

  /**
    * 英文日期
    */
  val dateFormat = DateTimeFormat.forPattern("dd/MMM/yyyy:HH:mm:ss Z")

  /**
    * 获取小时
    * 15/Feb/2017:00:54:09 +0800
    *
    * @param dataTime
    * @return
    */
  def getHour(dataTime: String): String = {
    val parseDate = DateTime.parse(dataTime, dateFormat.withLocale(Locale.ENGLISH))
    parseDate.toString("yyyyMMddHH")
  }


}
