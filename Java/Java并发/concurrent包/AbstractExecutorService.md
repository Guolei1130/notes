### 1.简介
>实现了ExecutorService的方法，这个类，实现了submit、invokeAny方法并返回newTaskFor，（这个类是这个包下FutureTask的默认实现）。例如，submit（RUnnable）的实现，创建一个RunnableFuture的实现，执行并且返回。子类可以override newTaskFor方法，并且返回另外一个实现。

### 2.相关方法

* newTaskFor 返回一个FutureTask
* submit方法 内部生成RunnableFuture，并execute，future返回。
* doInvokeAny 

