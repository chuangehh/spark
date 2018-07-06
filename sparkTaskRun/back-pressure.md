#背压机制
## spark1.5以前
* 限制Receiver的接收速率,配制 spark.streaming.receiver.maxRate
* producer数据生产高于maxRate,当前集群处理能力也高于maxRate，资源利用率下降等问题

## spark1.5后引入反压机制
* spark.streaming.backpressure.enabled来控制是否启用backpressure机制，默认值false
* 加上一个新的组件RateController
    * 这个组件负责监听OnBatchCompleted事件
    * 从中抽取processingDelay 及schedulingDelay信息
    * Estimator依据这些信息估算出最大处理速度（rate）
    * 基于Receiver的Input Stream将rate通过ReceiverTracker与ReceiverSupervisorImpl转发给BlockGenerator

