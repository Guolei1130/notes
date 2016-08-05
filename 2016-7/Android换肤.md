Android实现换肤的相关资源
_ _ _
##### 相关文章
* [Android换肤技术总结-zhaiyifan](http://blog.zhaiyifan.cn/2015/09/10/Android%E6%8D%A2%E8%82%A4%E6%8A%80%E6%9C%AF%E6%80%BB%E7%BB%93/)
* [Android主题换肤 无缝切换](http://www.jianshu.com/p/af7c0585dd5b)


##### 相关开源库
* [ThemeSkinning](https://github.com/burgessjp/ThemeSkinning)
* [MulipleTheme](https://github.com/dersoncheng/MultipleTheme)
* [Colorful](https://github.com/bboyfeiyu/Colorful)
* [Android-Skin-Loader](https://github.com/fengjundev/Android-Skin-Loader)
* [B站出品](https://github.com/Bilibili/MagicaSakura)

##### 相关开源库及其原理
1.MulipleTheme
设置theme，遍历控件，设置属性(那么多自定义的控件)
2.Colorful
和上面的原理一样
3.Android-Skin-Loader
关键类如下：
* SkinManager
* BaseActivity
* SkinInflaterFactory
* BackgroundAttr

SkinManager加载外部资源进来。
BaseActivity设置SkinInflaterFactory进来，当有变化时 调用view逐个设置属性


