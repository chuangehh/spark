package com.lcc.stream

import org.apache.spark.SparkConf
import org.apache.spark.streaming.{Seconds, StreamingContext}

/**
  * stream word count
  *
  * @author liangchuanchuan
  */
object StreamingWordCount {

  def main(args: Array[String]): Unit = {

    // 1.streaming context
    val conf = new SparkConf().setMaster("local[*]").setAppName("StreamingWordCount")
    val streamingContext = new StreamingContext(conf, Seconds(5))

    // 2.create DStream
    val lines = streamingContext.socketTextStream("hadoop101", 9999)

    // 3.word count
    val results = lines.flatMap(_.split("\\s+")).map((_, 1)).reduceByKey(_ + _)
    results.print()

    // 4.start the stream job
    streamingContext.start()

    // 5.wait for stream job to terminate
    streamingContext.awaitTermination()
  }

}