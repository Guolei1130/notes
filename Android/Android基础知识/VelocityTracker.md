速度跟踪器
>通过实现 fliging和其他类似的收拾来帮助追踪touch events的速度。

使用方法。

1. obtain() 取得实例
2. addMovement() 添加事件
3. computeCurrentVelocity 计算当前速度
4. getXVelocity/getYVelocity 取得速度
5. recycle() 回收

_ _ _

#### Public methods

* addMovement
* clear 恢复到初始化状态
* computeCurrentVelocity(int utils) 计算单位时间内的速度
* computeCurrentVelocity(int units, float maxVelocity) 计算单位时间内的速度
* 相关的get方法  获取速度
* obtain 实例化
* recycle 回收



