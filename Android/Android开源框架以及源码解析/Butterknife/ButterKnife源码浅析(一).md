### 1.0 前言
ButterKnife是我们日常开发当中常用的开源库，是一个编译时注解库。了解其原理对我们的提升十分有效。

### 2.AutoService
[开源地址](https://github.com/google/auto/tree/master/service)

这个的作用是什么，简单点来说就是省去我们的那些需要在resources/MATE.INF.services 下配置的一些东西，比如注解解释器等等，关于这个的用法朋友们去开源地址自己学习下吧，

### 3.AutoCommon

这个和上面那个都是在google的Auto下面的。这里包含一系列公共的util帮助我们很简单的使用annotation processing环境。

[开源地址](https://github.com/google/auto/tree/master/common)

### 4.Javapoet

[开源地址](https://github.com/square/javapoet)

是一些列帮助我们生成.java源文件的api，是不是很有意思？开源项目的md中给我们举了一些很有意思的例子，感兴趣的去看看吧。

### 5.要学习那些代码呢？
因为本人对测试不了解，所以略过了测试相关的内容。

* butterknife   一些api
* butterknife-annotations  一些注解
* butterknife-compiler  解释器
* butterknife-gradle-plugin  插件

### 6.butterknife-annotations
这是butterknife中定义的一些注解。

#### 6.1 internal包
这个包中有ListenerClass和ListenerMethod两个注解。其中Class的Target是ANNOTATION_TYPE，也就是对类、接口、枚举有效等，而Method的Target是Field，也就是对字段有效。

ListenerClass的一些属性：

* targetType 类型信息（是类还是接口还是其他）
* setter 设置targetType的什么方法
* remove remove掉targettype的方法，如果没有setter，则返回default
* type  完全限定类型的type
* method 当以的监听方法回调，有多个回调
* NONE 默认的callbacks

ListenerMethod的一些属性

* name 监听方法的名称
* parameters 监听方法的参数
* returnType 返回类型
* defaultReturn 如果returnType不返回void的并且没有绑定其他的话就返回这个

#### 6.2 一些注解
在这里呢，大致有两种注解，1.没有拿internal里面的修饰的，2。用里面的修饰了的。我们分别来举个例子。
最熟悉的BindString
```
@Retention(CLASS) @Target(FIELD)
public @interface BindString {
  /** String resource ID to which the field will be bound. */
  @StringRes int value();
}
```

很简单，当然，我们要注意到其中的@StringRes注解，这是support-annotations包里面的注解，目的是约束，就像这里，这里就只能用R.string.xxx来绑定。

最熟悉的Onclick。
```
@Target(METHOD)
@Retention(CLASS)
@ListenerClass(
    targetType = "android.view.View",
    setter = "setOnClickListener",
    type = "butterknife.internal.DebouncingOnClickListener",
    method = @ListenerMethod(
        name = "doClick",
        parameters = "android.view.View"
    )
)
public @interface OnClick {
  /** View IDs to which the method will be bound. */
  @IdRes int[] value() default { View.NO_ID };
}
```

这写法，逼格！是不是get到新能级了，反正我是get到了。
简单的解释下上面的写法。

给targetType set setter方法，全成为type的值，方法的方法名为doclick，参数为View，是不是有那么意思顿悟。

* 注意，OnClick中的属性是一个数组。

上面的DebouncingOnClickListener是什么鬼呢。我们来看下。

```
public abstract class DebouncingOnClickListener implements View.OnClickListener {
  static boolean enabled = true;

  private static final Runnable ENABLE_AGAIN = new Runnable() {
    @Override public void run() {
      enabled = true;
    }
  };

  @Override public final void onClick(View v) {
    if (enabled) {
      enabled = false;
      v.post(ENABLE_AGAIN);
      doClick(v);
    }
  }

  public abstract void doClick(View v);
}
```
,可以看的出，这里是防止多次点击的，并且doClick函数也对上了。欲知后事如何，请听下回分解。

