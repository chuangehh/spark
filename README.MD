# spark 

### spark集群角色
* Driver 提交任务,向Cluster获取资源(Executor)
* Cluster 分配任务到Worker节点,维护Worker节点,Driver状态
* Worker 负责具体业务运行,创建资源Executor
* Executor 分配给Driver的资源,执行Driver的Task

### 集群搭建
* 下载spark并解压到制定目录下
* 将slaves.template复制为slaves，编辑Worker host
    ```
    hadoop102
    hadoop103
    ```
* 将spark-env.sh.template复制为spark-env.sh，添加配置
    ```
    SPARK_MASTER_HOST=hadoop101
    SPARK_MASTER_PORT=7077
    ```
* 分发配置
* sbin/start-all.bash

### 配置历史服务
* 将spark-default.conf.template复制为spark-default.conf，开启Log
    ```
    spark.eventLog.enabled  true
    spark.eventLog.dir       hdfs://hadoop101:9000/sparkHistory
    spark.eventLog.compress true
    ```
* spark-env.sh 添加配置
    ```
    export SPARK_HISTORY_OPTS="-Dspark.history.ui.port=4000 -Dspark.history.retainedApplications=3 -Dspark.history.fs.logDirectory=hdfs://hadoop101:9000/sparkHistory"
    ```
* 分发配置文件
* 重启spark集群
* sbin/start-history-server.sh

### 配置spark HA
* zookeeper集群
* spark-env.sh 修改
    ```
    # SPARK_MASTER_HOST=hadoop101
    export SPARK_DAEMON_JAVA_OPTS="-Dspark.deploy.recoveryMode=ZOOKEEPER -Dspark.deploy.zookeeper.url=hadoop101:2181,hadoop102:2181,hadoop103:2181 -Dspark.deploy.zookeeper.dir=/spark"
    ```
* 分发配置文件
* 重启spark集群
* 观察zookeeper中的数据 get /spark/master_status

### 配置spark yarn
* 修改yarn-site.xml
    ```xml
    <configuration>
        <property>
            <name>yarn.resourcemanager.hostname</name>
            <value>hadoop103</value>
        </property>
        <property>
            <name>yarn.nodemanager.aux-services</name>
            <value>mapreduce_shuffle</value>
        </property>
        <!--是否启动一个线程检查每个任务正使用的物理内存量，如果任务超出分配值，则直接将其杀掉，默认是true -->
        <property>
            <name>yarn.nodemanager.pmem-check-enabled</name>
            <value>false</value>
        </property>
        <!--是否启动一个线程检查每个任务正使用的虚拟内存量，如果任务超出分配值，则直接将其杀掉，默认是true -->
        <property>
            <name>yarn.nodemanager.vmem-check-enabled</name>
            <value>false</value>
        </property>
    </configuration>
    ```
* 修改spark-env.sh，让Spark能够发现Hadoop配置文件
    ```
     HADOOP_CONF_DIR=/opt/module/hadoop-2.7.2/etc/hadoop
     YARN_CONF_DIR=/opt/module/hadoop-2.7.2/etc/hadoop
    ```
* 执行程序测试
    ```
    bin/spark-submit --class org.apache.spark.examples.SparkPi \ 
    --master yarn --deploy-mode client \ 
    examples/jars/spark-examples_2.11-2.1.1.jar 100    
    ```
* 查看History 运行结果以及运行环境

### spark应用提交
   ```
   ./bin/spark-submit \
     --class <main-class>
     --master <master-url> \
     --deploy-mode <deploy-mode> \
     --conf <key>=<value> \
     ... # other options
     <application-jar> \
     [application-arguments]
     
    1)--class: 你的应用的启动类 (如 org.apache.spark.examples.SparkPi)
    2)--master: 集群的master URL (如 spark://23.195.26.187:7077)
    3)--deploy-mode: 是否发布你的驱动到worker节点(cluster) 或者作为一个本地客户端 (client) (default: client)*
    4)--conf: 任意的Spark配置属性， 格式key=value. 如果值包含空格，可以加引号“key=value”. 缺省的Spark配置
    5)application-jar: 打包好的应用jar,包含依赖. 这个URL在集群中全局可见。 比如hdfs:// 共享存储系统， 如果是 file:// path， 那么所有的节点的path都包含同样的jar.
    application-arguments: 传给main()方法的参数 
     
   ```
   
### spark shell
* 启动
    ```
    /home/bigdata/hadoop/spark-2.1.1-bin-hadoop2.7/bin/spark-shell \
    --master spark://master01:7077 \
    --executor-memory 2g \
    --total-executor-cores 2
    ```
* master参数如果不写则为local模式
* shell中自动创建SparkContext，引用为sc,可在scala中直接使用

### RDD 弹性分布式计算集
* 不可变：在某一个RDD上执行转换操作，不会直接修改这个RDD数据，产生一个新的RDD
* 可分区：一个RDD的数据是分区存放的，所有分区都存在不同的Excutor中
* 弹性： 
    * 存储弹性
    * 计算弹性
    * 分片弹性
    * 容错弹性

### 创建RDD
* 从集合中创建RDD
    ```
    makeRDD[T: ClassTag](seq: Seq[T],numSlices: Int = defaultParallelism)
    parallelize[T: ClassTag](seq: Seq[T],numSlices: Int = defaultParallelism) 
    ```
* 默认分片机制 
    ```
        conf.getInt("spark.default.parallelism", math.max(totalCoreCount.get(), 2))
    ```

### RDD转换
* 对于数值型RDD,定义在RDD中
* 对于kv型RDD,定义在PairRDDFunctions中