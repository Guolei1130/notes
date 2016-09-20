[线程基础](http://blog.csdn.net/ecjtuxuan/article/details/4371887)
_ _ _

一旦涉及到线程，其运行效果就是不确定的。因为完全交给了调度器去运行。

### 线程的状态

* 新状态 实例化了对象，但是没有调用其start方法
* 就绪状态 调用了start，但是没有运行
* 运行状态 
* 等待、阻塞、睡眠状态  线程依然是活的，但是缺少运行条件
* 死亡状态 run结束之后，但是，这时候仍然不会被回收

### 阻止线程运行

* sleep 睡眠
* 线程优先级和让步 
	* 静态的Thread.yield方法
	* 非静态的join方法

### 线程的回收
步骤如下：
 1. interrupt 中断
 2. join

### 从任务中产生返回值
Callback接口替代Runnable接口

### 线程的优先级

* setPriority 
* getPriority

### 后台线程

必须在启动之前，调用setDaemon方法。






















