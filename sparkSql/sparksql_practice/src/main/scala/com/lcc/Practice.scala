package com.lcc

import org.apache.spark.SparkConf
import org.apache.spark.sql.SparkSession

/**
  * 货品交易
  *
  * @author liangchuanchuan
  */
object Practice {

  def main(args: Array[String]): Unit = {


    // sparkSession
    val conf = new SparkConf().setAppName("spark_sql_practice").setMaster("local[*]")
    val spark = SparkSession.builder().config(conf).getOrCreate()

    import spark.implicits._

    // create table
    val tbDate = spark.read.csv("F:\\scalaProject\\spark\\sparkSql\\doc\\tbDate.txt").toDF("dateid", "years", "theyear", "month", "day", "weekday", "week", "quarter", "period", "halfmonth")
    val tbStock = spark.read.csv("F:\\scalaProject\\spark\\sparkSql\\doc\\tbStock.txt").toDF("ordernumber", "locationid", "dateid")
    val tbStockDetail = spark.read.csv("F:\\scalaProject\\spark\\sparkSql\\doc\\tbStockDetail.txt").toDF("ordernumber", "rownum", "itemid", "number", "price", "amount")

    tbDate.createTempView("tbDate")
    tbStock.createTempView("tbStock")
    tbStockDetail.createTempView("tbStockDetail")


    //2.统计每年最大金额订单的销售额:
    //3.统计每年最畅销货品（哪个货品销售额amount在当年最高，哪个就是最畅销货品）


  }

}

