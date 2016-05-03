Android在java.util包之外增加了一些自身的实现，为了解决性能问题
* LruCache
* SparseArray
* SparseBooleanArray
* SparseIntArray
* Pair

应该熟悉java.util 和 android.util包。

### 优化

几个降低布局复杂性的步骤如下：
 1. 使用RelativeLayout代替嵌套LinearLayout，尽可能保持“扁平化”的布局，
 2. 使用ViewStub推迟对象创建

优化的基本原则是保持应用的持续响应。

SQLite
* SQLite语句
 * 加快SQL语句字符串的创建速度，StringBuffer(有提升) 和String.format
 * 语句编译,(?,?) 那种形式
 * DatabaseUtils.InsertHelper在数据库中传入多行，但是只需要编译一次SQL语句
* 事物
 * 显示创建事物有以下两个基本特征
  	* 原子提交
  	* 性能更好  
* 查询
 * 限制数据库访问的方式来加快查询速度 


### 合理使用内存
垃圾收集-----以下5中情况会触发垃圾收集
* GC_FOR_MALLOC 发生在堆被占满不能进行内存分配时，在分配新对象之前必须进行内存回收
* GC_CONCURRENT 发生在(可能是部分的)垃圾可供回收时，通常有很多对象可以回收
* GC_EXPLICIT 显式调用System.gc()产生的垃圾收集
* GC_EXTERNAL_ALLOC honeycomb及以上版本不会出现(一切都已在堆中分配)
* GC_HPROF_DUMP_HEAP 发生在创建HPROF文件时

Android中定义了几个API，可以通过它们来了解系统中还剩多少可用内存和用了多少内存
* ActivityManager 的getMemoryInfo()
* ActivityManager 的getMemoryClass()
* ActivityManager 的getLargeMemoryClass()

在manifest文件中把android:largeHeap设为true，就可以让应用使用更大的堆


### 多线程和同步
run方法，在当前线程中被调用执行，换言之，没有产生新线程。

设置线程优先级---Thread.setPriority()
* MIN_PRIORITY(1)
* NORM_PRIORITY(5)
* MAX_PRIORITY(10)

另一种设置线程优先级的方法是基于Linux优先级，使用android.os包里的Process.setThreadPriority，他定义了8个优先级。
___
java.util.concurrent包中定义了许多用于并发的类

_ _ _
获取处理器核心数量
```
Runtime.getRuntime().availableProcessors()
```

### 图形
1. 布局优化
 * 使用相对布局减少层级 
 * <merge/>标签合并布局
 * include 比起爱你重用布局
 * viewstub推迟实例化

### RenderScript

