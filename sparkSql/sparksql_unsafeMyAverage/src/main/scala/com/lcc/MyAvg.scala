package com.lcc

import org.apache.spark.sql.Row
import org.apache.spark.sql.expressions.{MutableAggregationBuffer, UserDefinedAggregateFunction}
import org.apache.spark.sql.types._

/**
  * 自定义平均值聚合函数
  *
  * 1.定义
  * 2.注册 sparkSession.udf
  *
  * example:
  * 平均值 = 总值 / 数量
  * avg = sum / count
  *
  * distributed computation:
  * partition_1: (sum:0,count:0) + (sum:0+100,count:0+1) + (sum:50+100,count:1+1)
  * partition_2: (sum:0,count:0) + (sum:0+100,count:0+1) + (sum:50+100,count:1+1)
  * partition_n: (sum:0,count:0) + (sum:0+100,count:0+1) + (sum:50+100,count:1+1)
  *
  * merge partition: partition_1 + partition_2 + partition_n = (sum:450,count:6)
  * result: return sum:450 / count: 6
  *
  * @author liangchuanchuan
  */
class MyAvg extends UserDefinedAggregateFunction {

  /**
    * 输入数据类型
    *
    * @return
    */
  override def inputSchema: StructType = StructType(StructField("salary", LongType) :: Nil)

  /**
    * 分区内数据类型
    *
    * @return
    */
  override def bufferSchema: StructType = StructType(StructField("sum", LongType) :: StructField("count", IntegerType) :: Nil)

  /**
    * 合并结果值类型
    *
    * @return
    */
  override def dataType: DataType = DoubleType

  /**
    * 是否幂等
    *
    * @return
    */
  override def deterministic: Boolean = true

  /**
    * 初始化分区内数据
    *
    * @param buffer
    */
  override def initialize(buffer: MutableAggregationBuffer): Unit = {
    buffer(0) = 0L
    buffer(1) = 0
  }

  /**
    * 分区内数据更新
    *
    * @param buffer
    * @param input
    */
  override def update(buffer: MutableAggregationBuffer, input: Row): Unit = {
    buffer(0) = buffer.getLong(0) + input.getLong(0)
    buffer(1) = buffer.getInt(1) + 1
  }

  /**
    * 所有分区数据合并
    *
    * @param buffer1
    * @param buffer2
    */
  override def merge(buffer1: MutableAggregationBuffer, buffer2: Row): Unit = {
    buffer1(0) = buffer1.getLong(0) + buffer2.getLong(0)
    buffer1(1) = buffer1.getInt(1) + buffer2.getInt(1)
  }

  /**
    * 获取合并结果值
    *
    * @param buffer
    * @return
    */
  override def evaluate(buffer: Row): Any = {
    buffer.getLong(0).toDouble / buffer.getInt(1)
  }
}
