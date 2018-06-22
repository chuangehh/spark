package com.lcc

import org.apache.spark.SparkConf
import org.apache.spark.sql.SparkSession

/**
  * 自定义聚合函数
  *
  * @author liangchuanchuan
  */
object TestAvg {

  def main(args: Array[String]): Unit = {
    val conf = new SparkConf().setAppName("udaf").setMaster("local[*]")
    val spark = SparkSession.builder().config(conf).getOrCreate()

    // 导入隐式函数
    import spark.implicits._

    spark.udf.register("myAvg", new MyAvg)
    val df = spark.read.json("F:\\scalaProject\\spark\\sparkSql\\doc\\employees.json")
    df.createOrReplaceTempView("employees")

    spark.sql("SELECT myAvg(salary) from employees").show()

    spark.stop()
  }

}
