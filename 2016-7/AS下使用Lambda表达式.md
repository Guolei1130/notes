1. 添加retrolambda插件
```
classpath 'me.tatarka:gradle-retrolambda:3.2.5'
```
2. 在项目中引用
```
apply plugin: 'me.tatarka.retrolambda'
```
3. 配置

```
    compileOptions {
        sourceCompatibility JavaVersion.VERSION_1_8
        targetCompatibility JavaVersion.VERSION_1_8
    }

    retrolambda {
        javaVersion JavaVersion.VERSION_1_6
    }
```