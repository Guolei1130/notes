
##### 1.什么是内存泄露
java中的内存泄露是指一个无用对象持续占有内存或无用对象的内存得不到及时的释放，从而造成的内存控件的浪费称为内存泄露。也就是说一个你不需要的对象竟然还占着内存，还不释放，GC也不顶用。

##### 2.Android中有哪些常见的内存泄露

1. ** 单例使用不当 **

	```
public class Singleton {
    private static Singleton instance = null;

    private Singleton() {
    }
    public static Singleton getInstance(){
        if (null == instance){
            synchronized (Singleton.class){
                if (null == instance){
                    instance = new Singleton();
                }
            }
        }
        return instance;
    }
}
```

	由于静态变量在整个程序的运行过程都存在内存中，因此，假如我们将context传入的时候就造成内存泄露，因为context被一个生命周期长的对象引用的不到释放。
** 解决办法 **
将Application传入。

2. ** 非静态内部类创建静态实例造成 **
因为内部类持有外部的引用，当内部类实例为static时，会导致外部类得不到释放。

	```
public class MainActivity extends AppCompatActivity {
    private static text t;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        t = new text();
    }

    public class text{

    }
}
```
** 解决办法 **
将内部类设为static，这样就可以保证外部activity可以释放了。

3. ** Handler、Thread、Timer等造成的内存泄露 **
当我们用如下方法使用handler的时候，发现变黄了，这是android studio在告诉我们，这 可能存在内存泄露，原因是我们在handler中可能进行耗时操作
	```
    public Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
        }
    };
    ```
这里给一张变黄的图。
** 解决办法 **
 * 使用静态内部类
 * 使用外部类的方式
 * 使用WeakReference
 在这里我们看下第三种方法的写法。
 ```
 public class MyHandler extends Handler{
        private WeakReference<Activity> weakReference;

        public MyHandler(Activity context){
            weakReference = new WeakReference<Activity>(context);
        }

        @Override
        public void handleMessage(Message msg) {
            if (null != weakReference.get()){
                // TODO: 16-4-9 do something
            }
        }
    }
 ```
 
 	Thread引起的内存泄露处理方法，在onDestory中回收即可。
 ```
 @Override
    protected void onDestroy() {
        super.onDestroy();
        if (null != mThread){
            try {
                mThread.interrupt();
                mThread.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
 ```
 Timer引起的内存泄露，在onDestory中cancel即可。
 
 	** 如果是view的话，将相关的操作放在onDetachFromWindow()中 **
    
4. ** WebView造成的内存泄露 **
没错，WebView是存在内存泄露的，那么我们如何解决呢。因国内各大厂商的深度定制ROM，webview每个版本都有区别。我在这里不敢保证我的这中方法没有问题，因为网上流传的解决办法，在我的红米1s上是不行的。
** 解决办法 **
 * 通过显示调用添加webview，并且在onDestory中移除销毁。
 ```
 	   layout.removeView(mWebView);
        mWebView.removeAllViews();
        mWebView.destroy();
 ```
 * 制作一个webviewactivity，将其运行在其他进程当中，用多进程的方式去解决。(最好的办法)

5. ** 资源类对象未关闭造成的内存泄露 **
如cursor、stream等造成的。
** 解决办法 **
及时关闭即可。

6. 注册类型的造成的内存泄露
如广播等，没及时解除注册，EventBus等。
** 解决办法 **
在适当的时候解除注册。

7. 其他
其他的一些诸如静态集合类等就不多做描述了。

##### 3.如何检查代码中的内存泄露
虽然我们知道了内存泄露的原因以及Android中常见的内存泄露，但是，在我们刻意的注意之下，仍然会有一些比较难发现的内存泄露，这个时候，我们就需要一些工具去检查。

* Android Monitor
在Memory中，我们可以观察到内存的使用情况，当我们在销毁一个activity的时候，假如发现内存并没有下降，或者GC都无法下降的时候，我们就可以确定这里存在内存泄露。

* LeakCanary
[github地址](https://github.com/square/leakcanary)
这个库用法非常简单，他会分析内存的情况，来判断是否有内存泄露，并会在leaks中罗列出所有的内存泄露并定位到详细对象，使用这个可以发现App中很大部分的内存泄露。

* Eclipse Memory Analyzer(MAT)
MAT的使用比较复杂，本篇暂时不说，下一篇博客中将会详细介绍MAT的使用。


##### 4. 总结
内存泄露是我们常见的优化部分，千里堤坝毁于蚁穴，我们要在平常的开发过程中去注意到这些东西，最后推荐几篇文章给大家看。
* [内存泄露从入门到精通三部曲之基础知识篇](http://bugly.qq.com/bbs/forum.php?mod=viewthread&tid=21&highlight=%E5%86%85%E5%AD%98%E6%B3%84%E9%9C%B2)
* [内存泄露从入门到精通三部曲之排查方法篇 ](http://bugly.qq.com/bbs/forum.php?mod=viewthread&tid=62&highlight=%E5%86%85%E5%AD%98%E6%B3%84%E9%9C%B2)
* [内存泄露从入门到精通三部曲之常见原因与用户实践 ](http://bugly.qq.com/bbs/forum.php?mod=viewthread&tid=125&highlight=%E5%86%85%E5%AD%98%E6%B3%84%E9%9C%B2)

[Android性能优化之常见的内存泄漏](http://android.jobbole.com/82198/)
