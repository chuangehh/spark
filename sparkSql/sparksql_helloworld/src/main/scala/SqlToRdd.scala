import org.apache.spark.sql.SparkSession

object SqlToRdd {

  def main(args: Array[String]): Unit = {
    val spark = SparkSession.builder()
      .appName("example")
      .master("local[*]")
      .getOrCreate()

    spark.read.json("F:\\scalaProject\\spark\\sparkSql\\sparksql_helloworld\\src\\main\\resources\\student.json")
      .createOrReplaceTempView("student")

    spark.sql("select name from student where age > 18")
      .show()
  }

}
