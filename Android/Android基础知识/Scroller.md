Scroller

>封装了scrolling，可以通过scroller或者overscroller去收集数据产生一个滚动动画，监听滑动的便宜距离但是并不自动应用这些属性。

* Scroller
* OverScroller

### Scroller

#### public methos

* abortAnimation stop the animation
* computeScrollOffset 计算偏移结果，如果结束，返回false
* extendDuration 扩展scroll 动画
* fling 开始scroll
* forceFinished 设置finished字段的值
* getCurrVelocity 获取当前速度
* getCurrX/Y 获取当前xy的偏移量
* getDUration 返回这个要耗费的事件
* getFinalX/Y 获取scroll 将要结束的值
* getStartX/Y 获取开始的偏移量
* isFinished 判断是否结束
* setFinalX/Y 设置x，y
* setFriction 设置阻力
* startScroll 开始滑动

_ _ _
View的scrollBy 和scrollTO方法，一个是相当于当前，一个是移动到指定位置

### OverScroller

>超出的滚动的范围，大多数情况下只能作为Scroller的替代

有几个新加的方法。

* notifyHorizontalEdgeReached 告诉scroller我们达到的水平边界
* notifyVerticalEdgeReached 告诉 达到边界
* springBack 回弹效果










































