### 1.概述
CompletableFuture类提供了非常强大的Future的扩展功能，可以帮助我们简化异步编程的复杂性，提供了函数式编程的能力。可以通过回调的方式处理处理计算结果，并且提供了转换和组合CompletableFuture的方法。

### 2.一些方法

* get
* getNow 如果结果已经计算完成则返回结果或者抛出异常，否则返回给定的值
* join

调用get方法会傻等，可以调用complete完成计算，促发客户端等待。也可以completeExceptionally 抛出异常。

### 3.创建CompletableFuture对象化

* completedFuture 用来返回一个已经计算好的CompleteableFUture

下面四个静态方法用来为一段异步执行的代码创建CompletableFuture对象。

* runAsync
* runAsync 含异常
* supplyAsync
* supplyAsync 含异常

以Async结尾并且没有指定Executor的放到发会使用ForkJoinPool.commonpool作为他的线程一部执行结果。

### 4.计算结果完成时的处理

* whenComplete
* whenCompleteAsync
* whenCompleteAsync
* exceptionally

### 5.转化
CompletableFuture可以作为monad(单子)和functor。由于回调风格的实现，我们不必因为等待一个计算完成而阻塞着调用线程，而是告诉CompletableFuture当计算完成的时候请执行某个function。而且我们还可以将这些操作串联起来，或者将CompletableFuture组合起来。

* thenApply
* thenApplyAsync

### 6.纯消费(执行Action)
上面的方法是当计算完成的时候，会生成新的计算结果(thenApply, handle)，或者返回同样的计算结果whenComplete，CompletableFuture还提供了一种处理结果的方法，只对结果执行Action,而不返回新的计算值，因此计算值为Void:

* thenAccept
* thenAcceptAsync


_ _ _

[参考资料](http://colobu.com/2016/02/29/Java-CompletableFuture/)

--存档--



































