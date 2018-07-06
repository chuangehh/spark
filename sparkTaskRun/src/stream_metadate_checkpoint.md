# Spark Streaming metadata checkpoint
* 概述
    * spark streaming的checkpoint 目的是保证长时间运行的任务在意外挂掉后保证数据不丢失
    * checkpoint包含两种数据
        * metadata
        * data
* 如何checkpoint
    * 一个可靠的文件系统保证数据的安全性
        * streamingContext.checkpoint(checkpointDirectory)
    * jobGenerator在每一个batch事件后调用generateJobs方法,jobScheduler.submitJobSet提交任务后，执行doCheckpoint方法来保存metadata
    