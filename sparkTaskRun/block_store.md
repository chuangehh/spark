# spark数据存储
### 概述
* 分层
    * 通信层
        * master-slave结构来实现通信层
        * master和slave之间传输控制信息、状态信息
    * 存储层
        * disk或是memory上面
        * replicate到远端
* 模块交互
    * 提供了统一的操作类BlockManager        
* 各模块功能
    * CacheManager
        * RDD在进行计算的时候，通过CacheManager来获取数据，并通过CacheManager来存储计算结果
    * BlockManager
        * CacheManager在进行数据读取和存取的时候主要是依赖BlockManager接口来操作
        * BlockManager决定数据是从内存(MemoryStore)还是从磁盘(DiskStore)中获取
        * 数据写入本地的MemoryStore或DiskStore是一个同步操作
    * MemoryStore   
        * 负责将数据保存在内存或从内存读取
    * DiskStore
        * 负责将数据写入磁盘或从磁盘读入   
    * BlockManagerWorker  
        * 防止数据丢失的时候还能够恢复
        * 异步完成数据复制的操作 
    * ConnectionManager 
        * 负责与其它计算结点建立连接，并负责数据的发送和接收
    * BlockManagerMaster
        * 只运行在Driver Application所在的Executor
        * 记录下所有BlockIds存储在哪个SlaveWorker上
        * Slave worker需要通过BlockManager向BlockManagerMaster询问数据存储的位置，然后再通过ConnectionManager去获取
        
###  启动过程分析
* 各个存储模块在 SparkEnv.create中完成
    
      
        