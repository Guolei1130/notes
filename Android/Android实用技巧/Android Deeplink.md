### 1.什么是Deeplink
关于Deeplink技术，大家可以前往官方文档进去了解.[传送门](https://developer.android.com/training/app-indexing/deep-linking.html)  
简单点来说，deeplink，深度链接，就是允许在网页中，跳转到你应用中的一种技术。这种技术在现在很常见，举个例子，你在网页中浏览商品，然后点击购买，哈，跳到app中了，没错，这就是deeplink。

### 2.如何使用
想要使用，需要在配置文件中配置Intent Filters，其中包含以下元素。

* action，可以过滤google search达到的意图
* data ，可以指定一个或者多个，每个都是url的格式
* category 包括可浏览的类别，

这么干说有点模糊，我们看下官方的列子。
```
<activity
    android:name="com.example.android.GizmosActivity"
    android:label="@string/title_gizmos" >
    <intent-filter android:label="@string/filter_title_viewgizmos">
        <action android:name="android.intent.action.VIEW" />
        <category android:name="android.intent.category.DEFAULT" />
        <category android:name="android.intent.category.BROWSABLE" />
        <!-- Accepts URIs that begin with "http://www.example.com/gizmos” -->
        <data android:scheme="http"
              android:host="www.example.com"
              android:pathPrefix="/gizmos" />
        <!-- note that the leading "/" is required for pathPrefix-->
        <!-- Accepts URIs that begin with "example://gizmos” -->
        <data android:scheme="example"
              android:host="gizmos" />

    </intent-filter>
</activity>


```

### 3.DeepLinkDispatch
当前分析版本2.0.1.

在编译之后生成DeeplinkActivity、DeeplinkDelegate、DeepLinkLoader、DeepLinkResult，这里就不分析生成的过程了，很就简单。

其实DeepLinkActivity就是一个空壳，将activity委托给DeepLinkDelegate进行处理。当然，在DeepLinkDelegate中先进去load操作，就是将所有的注解都加载进来，然后进行 相应的处理，尽情转发操作。
多的我就不说了，生成的代码比较清晰易懂，没有什么难度。

值得注意的是，Url的解析操作用的是okhttp中的HttpUrl类/






















