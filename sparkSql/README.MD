# spark sql
### 什么是sparksql
* sparksql类似于hive，主要是将sql转换成为RDD程序来运行

### sparksql的抽象
* RDD(Spark1.0) -> DataFrame(Spark1.3) -> DataSet(Spark1.6)
* DataFrame = RDD + Schema 
    * DataFrame性能比RDD高
        * 定制化内存管理 off-heap
        * 优化的执行计划
    * dataFrame劣势在于编译器缺少类型安全检查，运行期检查
* DataSet 
    * 样例类使用
    * type DataFrame = Dataset[Row]
    
### 如何使用
* Spark-shell来使用,变量spark
* 程序代码
    ```scala
        import org.apache.spark.SparkConf
        import org.apache.spark.sql.SparkSession

        val conf  = new SparkConf().setAppName("sql").setMaster("local[*]")
        val spark = SparkSession.builder().config(conf).getOrCreate()
        val sc = spark.sparkContext    
        val df = spark.read.json("F:\\scalaProject\\spark\\sparkSql\\doc\\employees.json")
        //注意，导入隐式转换，
        import spark.implicits._
        //展示整个表
        df.show()
        //展示整个表的Scheam
        df.printSchema()
        //DSL风格查询
        df.filter($"salary" > 3300).show
        //SQL风格
        //注册一个表名
        df.createOrReplaceTempView("employee")
        //查询
        spark.sql("select * from employee where salary > 3300").show()
        spark.close()
    ```

### 三种数据集之间的转换
* RDD -> DataFrame
    * 通过 将RDD转换位  元组结构 toDF
        ```scala
        rdd.map{x =>
            val pa = x.split(",")
            (pa(0).trim, pa(1).trim)
        }.toDF("name", "age")
        ```
    * 反射,需要样例类
        ```scala
        case class Person(name:String,age:String)
        rdd.map{x=>
            val pa = x.split(","); 
            Person(pa(0).trim, pa(1).trim)
        }.toDF 
        ```
    * 编程方式
        ```scala
        val peopleRDD = spark.sparkContext.textFile("examples/src/main/resources/people.txt")
        
        // The schema is encoded in a string,应该是动态通过程序生成的
        val schemaString = "name age"
        
        // Generate the schema based on the string of schema   Array[StructFiled]
        val fields = schemaString.split(" ")
        .map(fieldName => StructField(fieldName, StringType, nullable = true))
        
        // val filed = schemaString.split(" ").map(filename=> filename match{ case "name"=> StructField(filename,StringType,nullable = true); case "age"=>StructField(filename, IntegerType,nullable = true)} )
        
        val schema = StructType(fields)
        
        // Convert records of the RDD (people) to Rows
        import org.apache.spark.sql._
        val rowRDD = peopleRDD
        .map(_.split(","))
        .map(attributes => Row(attributes(0), attributes(1).trim))
        
        // Apply the schema to the RDD
        val peopleDF = spark.createDataFrame(rowRDD, schema)
        ```
     
* DataFrame -> RDD
    * dataframe.rdd 
    * rdd如何读取数据
        * df.map(_.getString(0)).collect
        * df.map(_.getAs[String]("name")).collect

* RDD -> DataSet        
    * 反射     
        ```
        case class Person(name:String,age:String)
        rdd.map{x=>
            val pa = x.split(","); 
            Person(pa(0).trim, pa(1).trim)
        }.toDS
        ```
        
* DataSet -> RDD
    * ds.rdd
    * 直接读取对象属性
    
* DataFrame -> DataSet
    * 样例类
        ```
            case class Person(name:String,age:String)
            df.as[Person]
        ```

* DataSet -> DataFrame
    * ds.toDF
        
### UDF函数,UDAF函数
* UDF函数
   * 注册一个UDF函数   spark.udf.register("addname",(x:String) => "name:"+ x)
   * spark.sql("select addname(name) as name  from people").show
   
* UDAF函数
   * @see project spark.sparkSql.sparksql_unsafeMyAverage
   
### Hive对接
* 使用自带Hive
    * 如果master节点发现spark-warehouse, 删除metastore_db、spark-warehouse
    * bin/spark-shell --master spark://master01:7077  --conf spark.sql.warehouse.dir=hdfs://master01:9000/spark-warehouse
    * 只要发现生成了新的metestore_db那么下次连接，不需要设置 spark.sql.warehouse.dir 这个地址了
    
* 和已有的Hive对接
    * 将hive conf目录下的 hive-site.xml 文件复制到 spark  master节点上的conf目录下
    * 将mysql的驱动要放到spark的jar包目录下。【最好分发】
    * 重启spark集群
    * 通过sql访问hive仓库表数据

### sparkSql输入输出
* 输入
    * 简易模式
        * spark.read.json("") csv,parquet,orc,textfile,jdbc
        * mysql
            ```scala
            val connectionProperties = new Properties()
            	connectionProperties.put("user", "root")
            	connectionProperties.put("password", "hive")
            
                val jdbcDF2 = spark.read
                    .jdbc("jdbc:mysql://master01:3306/rdd", "rddtable", connectionProperties)
            ```
        
    * 完整模式
        * spark.read.format("json").load() csv,parquet,orc,textfile,jdbc
        * mysql
            ```scala
                val jdbcDF = spark.read.format("jdbc")
                .option("url", "jdbc:mysql://master01:3306/rdd")
                .option("dbtable", " rddtable")
                .option("user", "root")
                .option("password", "hive")
                .load()
           ```

### 输出
* 简易模式  
    * df.wrete.json() csv,parquet,orc,textfile,jdbc
* 完整模式
    * dataframe.write.format("json").mode(SaveMode.Overwrite).save() csv,parquet,orc,textfile,jdbc
        ```
            - `SaveMode.Overwrite`: overwrite the existing data.
            *   - `SaveMode.Append`: append the data.
            *   - `SaveMode.Ignore`: ignore the operation (i.e. no-op).
            *   - `SaveMode.ErrorIfExists`: default option, throw an exception at runtime.
        ```
    * mysql
        ```scala
            dataframe.write
                .format("jdbc")
                .mode("SaveMode.Append")
                .option("url", "jdbc:mysql://master01:3306/rdd")
                .option("dbtable", "rddtable2")
                .option("user", "root")
                .option("password", "hive")
                .save()
       ```