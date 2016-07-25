#### 1.Drawable Animation 帧动画
```
<animation-list xmlns:android="http://schemas.android.com/apk/res/android"
    android:oneshot="true">
    <item android:drawable="@drawable/rocket_thrust1" android:duration="200" />
    <item android:drawable="@drawable/rocket_thrust2" android:duration="200" />
    <item android:drawable="@drawable/rocket_thrust3" android:duration="200" />
</animation-list>
```
在代码或者XML文件中设置background即可。

#### 2.ViewAnimation View动画

##### 2.1 Tween Animation
* res/anim/filename.xml
* Java R.anim.filename
* XML @[package:]anim/filename
_ _ _
- interpolator 插值器
- shareInterpolator set标签下的 是否共用
- fromAlpha 0.0 -1.0
- pivotx 中心点，可以选百分比 % 和 p%
- fromXDelta % 和 p%的区别就是相对于自己和相对于父控件


##### 2.2 FrameAnimation 也就是1
#### 3.Property Animation 属性动画
* 三种根标签
 * set 对应AnimatorSet
  	- ordering {sequentially:顺序,together:一起，默认的属性}  
 * objectAnimator  对应ObjectAnimator
 	- prepertyName 属性名
 	- valueTo float，int  color，
 	- valueFrom
 	- duration int
 	- startOffset int 动画的延迟，毫秒 start（）之后
 	- repeatCount 重复次数
 	- repeatMode 重复模式
 	- valuetype 关键字，没有指定的话是颜色，将自己处理
 * animator  对应AnimatorSet
    - 和ObjectAnimator一样

* 资源路径 res/animator/filename.xml
* Java R.animator.filename
* XML @[package:]animator/filename

加载方法
AnimatorInflater.loadAnimator加载



#### 4. Interpolators
* AccelerateDecelerateInterpolator	@android:anim/accelerate_decelerate_interpolator 加速减速
* AccelerateInterpolator 加速	@android:anim/accelerate_interpolator
* AnticipateInterpolator 向后 然后向1前甩	@android:anim/anticipate_interpolator 
* AnticipateOvershootInterpolator 始的时候向后然后向前甩一定值后返回最后的值	@android:anim/anticipate_overshoot_interpolator
* BounceInterpolator	@android:anim/bounce_interpolator 动画结束的时候弹起
* CycleInterpolator	@android:anim/cycle_interpolator动画循环播放特定的次数，速率改变沿着正弦曲线
* DecelerateInterpolator	@android:anim/decelerate_interpolator在动画开始的地方快然后慢
* LinearInterpolator	@android:anim/linear_interpolator以常量速率改变
* OvershootInterpolator	@android:anim/overshoot_interpolator向前甩一定值后再回到原来位置
