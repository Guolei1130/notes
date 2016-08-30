#### 1.创建Observable对象
* Observable.create方法
* Observable.from方法 从列表中获取
* Observable.just方法 从传统的java函数中获取，将会发送函数的返回值。
* Observable.empty 毫无理由的不在发射数据正常结束时
* Observable.never 创建一个不发射数据并且也永远不会结束
* Observable.throw 不发射护具并且以错误结束

伪代码如下：
```
Observable<Integer> observable = Observable.create(new Observable.OnSubscribe<Integer>() {});
Observer observer = new Observer<Integer>(){

}
  observable.subscribe(observer);
```

#### 2.Subject = Observable + Observer
一旦Subject订阅了Observable，他将会触发Observable开始发射，如果原始的Observable是冷的，这将会对订阅一个热的Observable变量产生影响。

##### 2.1 PublishSubject
没有数据要发送，观察者只能等待，没有阻塞线程，也没有消耗资源。
用法：
* PublishSubject<Integer> publishSubject = PublishSubject.create();
* publishSubject.subscribe(observer);
* publishSubject.onNext(111);

更复杂的例子：
* 先创建PublishSubject来响应他的onNext方法。
* 在创建私有的Observable，在doxxx的方法里面调用PublishSubjct的onNext方法。

##### 2.2 BehaviorSubject
会首先向他的订阅者发送截止订阅前最新的一个数据对象(或初始值),然后正常发送订阅后的数据流。

##### 2.3 ReplaySubject
会缓存他所订阅的所有数据，向任意一个订阅他的观察者重复。

##### 2.4 AsyncSubject
当Observable完成时AsyncSubject只会发布最后一个数据给已经订阅的每一个观察者。


### 操作符
* repeat 重复发射数据次数
* defer 向申明一个Observable 但是你又想推迟这个Observable的创建知道观察者订阅时。
* range 从指定的数字X开始发射N个数字
* interval 需要创建一个轮询程序时非常好用，2此发射的时间间隔，另一个是用到的时间单位。
* timer 一个一段时间之后才发射的Observable

###过滤序列
* fliter过滤观测序列
* take、takeLast取开头和结尾的几个元素，takelat只能作用于一组有限的序列。
* distinct 去掉重复的
* distincuntilschanged 在一个可观察序列发射一个不同于之前的一个新值时让我们得到通知。
* first
* last
* firstOrDefault 如果不在发射任何值的时候，我们可以指定发射一个默认值。
* lastOrDefault
* skip and skilast，不发射。。。
* ElementAt 仅发射某个元素
* sample 指定的时间间隔里发射最近一次的值，如果我们想让它定时发射第一个元素而不是最近的一个元素，我们可以使用throttleFirst、
* TimeOut，如果在指定时间间隔内不发射值得花，就发射一个错误。
* Debounce 过滤掉发射的速率过快的数据，如果在一个指定的时间间隔过去了仍旧没有发射一个，那么他讲发射2个、

### 转换Observalbels
* map 变化
* FlatMap 他发射一个数据序列，这些数据学列本身也可以发射Observable，铺平序列的方式，然后合并这个Observables发射的数据，最后将合并后的结果作为最终的Observable
* ConcatMap 解决flatMap数据的交叉问题，
* FlatMapIterable 和flatmap很像，他将数据源两两结合成对并生成Iterable，而不是原始数据项和生成的Observables
* switchMap 和flatmap很像，每当源Observable发射一个新的数据项时，他将取消订阅病停止监视之前那个数据项产生的Observable，病开始监视当前发射的这一个。
* Scan 可以看做是一个累积函数，对原始的Observable发射的每一项数据都应用于一个函数，计算出函数的结果值，病将该值填充回可观测序列，等待和下一次发射的数据一起使用。（将值计算）
* GroupBy 排序
* buffer函数，将源Observable变换成一个新的Observable，这个新的Observable每次发射一组列表值而不是一个一个发射。
* window 和buffer很像，但是他发射的是Observable而不是列表
* cast 将源中的每一项数据都转换为新的类型，把它变成了不同的Class

### 组合Observables
* merge 合并到发射的数据项里
* mergeDelayError 继续发射数据即便是其中有一个抛出了错误
* zip 接收数据，处理他们，并发射新值
* Join 基于时间窗口，将2个Observables发射的数据结合在一起。
 * 第二个Observable 和源Observable结合
 * Func1 参数：在指定的由时间窗口定义时间间隔内，源Observab发射的数据和第二个Observable发射的数据相互配合返回的Observable。
 * Func2 参数：在指定的由时间窗口定义时间间隔内，而第二个Observable发射的数据和源Observable发射的数据相互配合返回的Observable
 * Func2 参数：定义已发射的数据如何与新发射的数据项结合
* combinelastest 有点像zip，作用于最近发射的数据项，1发射了A，2发射了BC,将分组处理AB,AC、
* And/Then/When
 * And 连接源Observable 和第二个Observable
 * Then 
 * When 当有了什么的时候
* switch 订阅一个新的Observable
* startWith通过传递一个参数来先发射一个数据序列


### 调度器Schedulers
* Schedulers.io 用户I/O操作
* Schedulers.computation 计算工作的默认的调度器
* Schedulers.immediate 允许你立即在当前线程执行你指定的工作
* Schedulers.newThread 启动一个新的线程
* Schedulers.trampolibe 入队，处理它的队列并且按序运行队列中的每一个任务。

_ _ _
* subscribeOn 用于每个Observable对象，数据源的线程
* observeon 用于每个Observser对象，观察者的线程

### 与REST无缝结合-RxJava和Retrofit


doOnNext 允许我们在输出之前做一些额外的操作。

## 可以和那些东西结合使用
* retrofit
* rxbinding
* rxlifecycle
* rx-preferences
* rxbus