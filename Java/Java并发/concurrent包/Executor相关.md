#### 1.设计到的类

* Executor
	* ExecutorService
		* AbstractExecutorService
			* ForkJoinPool
    * Executors
* Callable
* Feture
* CompletionService

#### 2.Callback、Future、Runnable接口

##### 2.1 Callback接口
一个task任务，能返回一个结果
##### 2.2 Future
异步的结果。

##### 2.3 Runnable接口
实现Thread的另一种方式。

#### 3.ExecutorService 任务调度器

>调度器的集中状态 运行、关闭、终止

* shutdown、shutdownNow, 进入关闭状态，不在接受新的任务，并且执行完已有任务
* isShutdown 判断是够是SHUTDOWN状态
* isTerminated 是否时终止状态
* awaitTermination 指定一段时间，要么执行完毕，要么超时，要么中断了。
* submit 提交任务，返回Future
* invokeAll 执行所有，当所有都完成的时候，或超时的时候，返回
* invokeAny 有一个成功就返回。

#### 4.Executors

用于构建一些线程池。除了四种一直提的线程池外，还有几种。

* newWorkStealingPool @since 1.8
* newSingleThreadScheduledExecutor ScheduledExecutorService。

##### 4.1 newWorkStealingPool
返回一个ForkJoinPool，这是1.7加入的，分叉结合框架。目的是将一个任务分成多个子任务，在进行汇总计算得合并的计算过程。实现了工作窃取算法。使得空闲线程主动承担从别的线程分出来的子任务，从而使所有的线程都处在饱和的工作状态，提高效率。
java中提供了两个实现。RecursiveTask 和 RecursiveAction

[参考链接](http://www.blogjava.net/shinzey/archive/2012/02/09/368312.html)

##### 4.2 ScheduledExecutorService

提供了时间排程的功能，可以延迟一段时间在执行。

#### 5.CompletionService

* submit 提交任务
* take 获取任务结果
* poll 获取任务结果

##### 5.1 ExecutorCompletionService

* 内部管理者一个已完成任务的阻塞队列
* 引用了一个Executor，用来执行任务
* submit方法最终会委托给内部的executor去执行任务
* taje/poll放啊的工作都委托给你不的已完成任务阻塞队列
* 吐过阻塞队列中有已完成的任务，就返回结果，否则阻塞任务外层。

![](http://upload-images.jianshu.io/upload_images/1642441-483dc853855a8ce1.png?imageMogr2/auto-orient/strip%7CimageView2/2)

[用法参考](http://www.jianshu.com/p/cfda708a3478)
_ _ _

[参考链接](http://www.open-open.com/solution/view/1320131360999)



