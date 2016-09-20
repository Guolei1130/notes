### 介绍
Exceutor框架在Java 5 中引入，内部使用了线程池机制，在java.util.cocurrent包线下，通过该框架来控制线程的启动、执行和关闭，可以简化并发编程的操作。
Exceutor框架包括一下内容：

* Exceutor
* Exceutors
* ExceutorService
* COmpletionService
* Future
* CallBack
* 等

Executor 接口中之定义了一个方法 execute（Runnable command），该方法接收一个 Runable 实例，它用来执行一个任务，任务即一个实现了 Runnable 接口的类。ExecutorService 接口继承自 Executor 接口，它提供了更丰富的实现多线程的方法，比如，ExecutorService 提供了关闭自己的方法，以及可为跟踪一个或多个异步任务执行状况而生成 Future 的方法。 可以调用 ExecutorService 的 shutdown（）方法来平滑地关闭 ExecutorService，调用该方法后，将导致 ExecutorService 停止接受任何新的任务且等待已经提交的任务执行完成(已经提交的任务会分两类：一类是已经在执行的，另一类是还没有开始执行的)，当所有已经提交的任务执行完毕后将会关闭 ExecutorService。因此我们一般用该接口来实现和管理多线程。

ExecutorService 的生命周期包括三种状态：运行、关闭、终止。创建后便进入运行状态，当调用了 shutdown（）方法时，便进入关闭状态，此时意味着 ExecutorService 不再接受新的任务，但它还在执行已经提交了的任务，当素有已经提交了的任务执行完后，便到达终止状态。如果不调用 shutdown（）方法，ExecutorService 会一直处在运行状态，不断接收新的任务，执行新的任务，服务器端一般不需要关闭它，保持一直运行即可。


### Exceutors

Exceutors 提供了一些列静态方法创建ExceutorService线程池对象。有以下几种类型。

* newFixedThreadPool 固定线程数目的线程池
* newCachedThreadPool 创建一个可缓存的线程池，调用execute将重用以前构造的线程(如果可用的话)，不可用则创建新的线程。
* newSingleThreadExecutor 单线程化的
* newScheduledThreadPool 创建一个支持定时及周期性的任务执行的线程池，多数情况下可用来替代Timer类

### Executor执行Runnable任务

```
import java.util.concurrent.ExecutorService;   
import java.util.concurrent.Executors;   

public class TestCachedThreadPool{   
    public static void main(String[] args){   
        ExecutorService executorService = Executors.newCachedThreadPool();   
//      ExecutorService executorService = Executors.newFixedThreadPool(5);  
//      ExecutorService executorService = Executors.newSingleThreadExecutor();  
        for (int i = 0; i < 5; i++){   
            executorService.execute(new TestRunnable());   
            System.out.println("************* a" + i + " *************");   
        }   
        executorService.shutdown();   
    }   
}   

class TestRunnable implements Runnable{   
    public void run(){   
        System.out.println(Thread.currentThread().getName() + "线程被调用了。");   
    }   
}  
```

### Executor执行Callback任务


### 自定义线程池

自定义线程池，可以用ThreadPoolExecutor类创建。

```
ThreadPoolExecutor pool = new ThreadPoolExecutor(3,5,50,TimeUnit.MILLISECONDS,bqueue);
```

* corePOolSize 核心线程数
* maximumPoolSize 池中允许的最大线程数
* keepAliveTime 空闲线程多能持续的最长时间
* util 时间单位
* workQueue 任务执行前，保存任务的队列

需要注意：

* 如果线程池中的线程数量少于核心线程数，即便有空闲线程，也会创建新的线程
* 如果线程池中线程数大于核心线程数，但是缓存队列workQueue未满，将新的任务方法放到队列中，FIFO原则等待执行
* 如果线程数大于核心线程但是小于最大线程数，切缓存队列满，创建新的线程
* 若想等

核心线程>等待队列>max-核心线程部分


### 几种排队的策略

* 直接提交 缓冲队列采用SynchronousQUeue
* 无界队列 LinkedBlockingQueue
* 有界队列 ArrayBlockingQueue

_ _ _

Excutor是接口

Excutors中

* 部分通过ThreadPoolExecutor 来创建ExcutorService(Java 5加入的)
* java 8中增加ForkJoinPool(1.7加入)

ThreadPoolExecutor继承了AbstractExecutorService，而AbstractExecutorService实现了ExceutorService接口

这几个的关系搞清楚了。

































