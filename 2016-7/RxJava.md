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


操作符
* repeat 重复发射数据次数
* defer 向申明一个Observable 但是你又想推迟这个Observable的创建知道观察者订阅时。
* range 从指定的数字X开始发射N个数字
* interval 需要创建一个轮询程序时非常好用，2此发射的时间间隔，另一个是用到的时间单位。
* timer 一个一段时间之后才发射的Observable

过滤序列
* fliter过滤观测序列