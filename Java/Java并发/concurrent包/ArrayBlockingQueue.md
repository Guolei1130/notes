### 1.类简介

>一个从array返回的有界的blocking queue，这个队列排列元素追寻FIFO原则。
>这是一个有界的缓存buffer，有固定容量，一旦创建，容量就不能改变了。当像一个full queue put数据的时候，会阻塞，take空queue的时候同样会阻塞。

### 2.类字段

* items 队列中所有的元素 Object[]
* takeIndex 取索引，(take、poll、peek、remove)
* putIndex 存索引（put,offer,add）
* count 队列元素的数量
* lock 重入锁
* notEmpty 等待waiting takes
* notFull 等待put
* itrs 当前活动迭代器的分享状态，如果知道没有任何的话就会是null。允许队列更新迭代器状态。

### 3.类方法


>对数组进行操作的方法都必须获取lock

* dec 循环递减
* itemAt 返回i处的item
* enqueue 在当前put position出插入元素，(进入和信号？)，只有在得到锁的时候被call
* dequeue，和enqueue相反，提取元素
* removeAt 删除元素
* add 在队列的结尾处插入一个特殊的元素
* offer 如果队列满了，返回false
* put 进入阻塞，直到队列有空闲，插入
* offer 等待一段时间，如果还是满，返回false
* poll 内部调用dequeue
* take 如果为空，进入阻塞。直到有元素，然后dequeue
* poll 等待一段时间
* peek 内部调用itemAt
* remainingCapacity 计算剩余容量
* remove
* contains 判断是否包含某元素
* toArray 转化为数组
* clear 原子操作移除所有的元素
* drainTo 排掉指定大小的元素

### 4.内部类Itrs

用于在迭代器和队列之间进行数据共享，当元素被移除之后，允许修改通知迭代器















































