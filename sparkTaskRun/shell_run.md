### spark脚本解析
* start-all.sh 
    * Start all spark daemons.
    * Starts the master on this node.
    * Starts a worker on each node specified in conf/slaves
    
* start-master.sh
    * CLASS="org.apache.spark.deploy.master.Master"
    * SPARK_MASTER_HOST="`hostname -f`"
    * SPARK_MASTER_PORT=7077
    * SPARK_MASTER_WEBUI_PORT=8080
    * spark-daemon.sh start $CLASS 1 --host $SPARK_MASTER_HOST --port $SPARK_MASTER_PORT --webui-port $SPARK_MASTER_WEBUI_PORT $ORIGINAL_ARGS
    
* start-slaves.sh
    * "${SPARK_HOME}/sbin/start-slave.sh" "spark://$SPARK_MASTER_HOST:$SPARK_MASTER_PORT"    
 
* start-slave.sh
    * CLASS="org.apache.spark.deploy.worker.Worker"
    * SPARK_WORKER_WEBUI_PORT=8081
    * spark-daemon.sh start $CLASS $WORKER_NUM --webui-port "$WEBUI_PORT" $PORT_FLAG $PORT_NUM $MASTER "$@"
    
* start-history-server.sh
    * spark-daemon.sh start org.apache.spark.deploy.history.HistoryServer 1 "$@"
    
* spark-daemon.sh
    * SPARK_LOG_DIR="${SPARK_HOME}/logs"
    * SPARK_PID_DIR=/tmp
    * rsync -a -e ssh --delete --exclude=.svn --exclude='logs/*' --exclude='contrib/hod/logs/*' "$SPARK_MASTER/" "${SPARK_HOME}"
    * bin/spark-class "$command" "$@"
    * bin/spark-submit --class "$command" "$@"

* bin/spark-submit
    * exec "${SPARK_HOME}"/bin/spark-class org.apache.spark.deploy.SparkSubmit "$@"
       
* bin/spark-class
    * RUNNER="${JAVA_HOME}/bin/java"
    * SPARK_JARS_DIR="${SPARK_HOME}/jars"
    * "$RUNNER" -Xmx128m -cp "$LAUNCH_CLASSPATH" org.apache.spark.launcher.Main "$@"
    * exec "${CMD[@]}" ;the result is "java -cp"

