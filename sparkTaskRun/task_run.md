#task 运行流程
* rdd action operator invoke sc.runJob(..) 
    ```scala
        def collect(): Array[T] = withScope {
            val results = sc.runJob(this, (iter: Iterator[T]) => iter.toArray)
        Array.concat(results: _*)
}
    ```
* dagScheduler invoke runJob(..)
    ```scala
        dagScheduler.runJob(rdd, cleanedFunc, partitions, callSite, resultHandler, localProperties.get)
    ```
* eventProcessLoop put eventQueue
    ```scala
        eventProcessLoop.post(JobSubmitted(
            jobId, rdd, func2, partitions.toArray, callSite, waiter,
            SerializationUtils.clone(properties)))
          
        def post(event: E): Unit = {
            eventQueue.put(event)
        }
    ```
* eventProcessLoop parent EventLoop new Thread invoke onReceive(event)
    ```scala
        private val eventThread = new Thread(name) {
            setDaemon(true)
        
            override def run(): Unit = {
              try {
                while (!stopped.get) {
                  val event = eventQueue.take()
                  try {
                    onReceive(event)
                  } catch {
                    case NonFatal(e) =>
                      try {
                        onError(e)
                      } catch {
                        case NonFatal(e) => logError("Unexpected error in " + name, e)
                      }
                  }
                }
              } catch {
                case ie: InterruptedException => // exit even if eventQueue is not empty
                case NonFatal(e) => logError("Unexpected error in " + name, e)
              }
            }
        
         }
    ```
* dagScheduler.handleJobSubmitted(..)
    ```scala
    override def onReceive(event: DAGSchedulerEvent): Unit = {
        val timerContext = timer.time()
        try {
            doOnReceive(event)
        } finally {
            timerContext.stop()
        }
    }
    
    private def doOnReceive(event: DAGSchedulerEvent): Unit = event match {
        case JobSubmitted(jobId, rdd, func, partitions, callSite, listener, properties) =>
        dagScheduler.handleJobSubmitted(jobId, rdd, func, partitions, callSite, listener, properties)
    }   
    ```
* find finalStage then submitMissingTasks
    ```scala
        val missing = getMissingParentStages(stage).sortBy(_.id)
        logDebug("missing: " + missing)
        if (missing.isEmpty) {
            logInfo("Submitting " + stage + " (" + stage.rdd + "), which has no missing parents")
            submitMissingTasks(stage, jobId.get)
        } else {
            for (parent <- missing) {
              submitStage(parent)
            }
            waitingStages += stage
        }
    ```
* taskScheduler.submitTasks
    ```scala
        backend.reviveOffers()
    ```
* CoarseGrainedSchedulerBackend  invoke driverEndpoint.send(ReviveOffers)
* DriverEndpoint
    ```scala
       executorData.executorEndpoint.send(LaunchTask(new SerializableBuffer(serializedTask)))
    ```
* 
    
    
    
    
