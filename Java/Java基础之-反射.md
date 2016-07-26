### 1. 获取Class对象
1. 方法一
```
Class<?> myObjectClass = MyObject.class;
```
2. 方法二
```
Student me = new Student("mr.simple");
Class<?> clazz = me.getClass();
```
3. 方法三
```
Class<?> myObjectClass = Class.forName("com.simple.User");
```

### 2.通过Class对象构造目标类型的对象
```
    private static void classForName() {
        try {
            // 获取 Class 对象
            Class<?> clz = Class.forName("org.java.advance.reflect.Student");
            // 通过 Class 对象获取 Constructor，Student 的构造函数有一个字符串参数
            // 因此这里需要传递参数的类型 ( Student 类见后面的代码 )
            Constructor<?> constructor = clz.getConstructor(String.class);
            // 通过 Constructor 来创建 Student 对象
            Object obj = constructor.newInstance("mr.simple");
            System.out.println(" obj :  " + obj.toString());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
```
### 3.获取构造函数接口
```
// 获取一个公有的构造函数，参数为可变参数，如果构造函数有参数，那么需要将参数的类型传递给 getConstructor 方法
public Constructor<T> getConstructor (Class...<?> parameterTypes)
// 获取目标类所有的公有构造函数
public Constructor[]<?> getConstructors ()
```

### 4.设置accessible 标志
将这个标志设为true来提升反射速度
```
learnMethod.setAccessible(true);
```

### 5.反射获取类中的函数
* getDeclaredMethods 获取所有的方法
* getDeclaredMethod(发发明，class) 获取当前类 和当前父类

_ _ _
```
// 获取 Class 对象中指定函数名和参数的函数，参数一为函数名，参数 2 为参数类型列表
public Method getDeclaredMethod (String name, Class...<?> parameterTypes)

// 获取该 Class 对象中的所有函数( 不包含从父类继承的函数 )
public Method[] getDeclaredMethods ()

// 获取指定的 Class 对象中的**公有**函数，参数一为函数名，参数 2 为参数类型列表
public Method getMethod (String name, Class...<?> parameterTypes)

// 获取该 Class 对象中的所有**公有**函数 ( 包含从父类和接口类集成下来的函数 )
public Method[] getMethods ()
```

调用方法。
```
learnMethod.invoke(student, "java");
```
### 6.反射类中的属性
* getField
* getDeclaredField 获取当前类和当前类的父类
调用set方法设置属性。

### 7.反射获取父类与接口
* getSupperclass 获取Class对象的父类
* getInterfaces 获取接口

### 8.获取注解信息
1.先获取字段
2.调用字段的getAnnotation方法，获取注解

* getAnnotations



