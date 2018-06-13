package com.atlcc

import org.apache.spark.{SparkConf, SparkContext}

/**
  * 统计一个文件中的单词数
  *
  * @author liangchuanchuan
  */
object WordCount {

  def main(args: Array[String]): Unit = {
    // conf
    val conf = new SparkConf().setAppName("wordCount").setMaster("local[*]")

    // spark context
    val sc = new SparkContext(conf)

    // word count
    val list = sc.textFile("./pom.xml")
      // 根据空格分割 文本
      .flatMap(_.split(" "))
      // 把每个设置为 对偶元组(key,1)
      .map((_, 1))
      // 根据对偶元组的key分组并把 key + 1
      .reduceByKey(_ + _)
      // 收集数据,转换为一个Array 数组
      .collect()

    // print word count
    println(list.foreach(println(_)))

    // stop
    sc.stop()
  }

}
