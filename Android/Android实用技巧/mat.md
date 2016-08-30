#### 使用MAT检查内存泄露

##### 1.MAT简单介绍
MAT(Memory Analyzer Tool) 是基于heap dumps来进行内存分析的工具，一个基于Eclipse的内存分析工具，是一个快速、功能丰富的JAVA heap分析工具，它可以帮助我们查找内存泄漏和减少内存消耗。
##### 2.下载安装
 * 下载地址 http://www.eclipse.org/mat/downloads.php
 >地址里面有独立安装包和插件包
 
 * 选择合适的版本下载安装
   * 这里可以选择插件和独立包下载 根据自己的需求进行下载，我这里选择的是zip包，通过eclipse去进行插件安装

##### 3.获取.hprof文件
* 在eclipse中打开DDMS，在左侧选择dump HPROF File按钮，暂停之后MAT就会自动打开，根据引导，直到出现如下界面。



* 在Android studio中打开Android Monitor面板，选择Memory，点击左侧dump java heap按钮dump内存信息，点击stop之后就会在android studio 中打开.hprof文件，打开所在的路径，通过hprof-conv来转化成MAT可以识别的.hprof文件，hprof-conv在SDK的platform-tools下，windos下双击启动，ubuntu下./hprof-conv启动，转化命令如下
```
hprof-conv 输入文件路径 输出文件路径
```
结果如下图：

##### 4.MAT界面元素

