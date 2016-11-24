### 使用Django搭建简易博客

[学习地址](http://wiki.jikexueyuan.com/project/django-set-up-blog/project-app.html)
_ _ _ 

#### 1. 安装django

```
pip install django

```

#### 2. 初始化

```
django-admin.py startproject my_blog
```

#### 3. 建立django app

```
cd my_blog
python manage.py startapp article
```
并在my_blog/my_blog/setting.py下添加新建app

```

INSTALLED_APPS = (
        ...
        'article',  #这里填写的是app的名称
    )
```

#### 4. 运行

```
 python manage.py runserver   #启动Django中的开发服务器
 
 如果有错误，则先
 python manage.py migrate
```