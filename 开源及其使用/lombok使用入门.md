#### 1.Lombok
自动生成get/set方法，简化bean类。
#### 2.如何介入
AS中先安装Lombok插件，重启AS，并在Gradle文件中添加
```
compile 'org.projectlombok:lombok:1.16.8'
```

#### 3.几个注解
* @Getter 生成get方法
* @Setter 生成set方法
* @NonNull 不为null(会抛异常)
* @ToString toString方法
 * callSuper = true，指调用super.toString
 * exclude 去除不需要的字段
* @EnqualsAndHashCode 生成enueal和hashcode方法
 * callSuper 调用supper
 * exclude 去除哪些字段
* @Data
 * staticConstructor=“方法名” 生成个静态单例
* @Cleanup 关闭资源如fos之类
* @Synchronized 方法加锁
* @SneakyThrows 异常处理

#### 4.文档地址
[文档地址](http://jnb.ociweb.com/jnb/jnbJan2010.html)