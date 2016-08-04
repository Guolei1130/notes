##### 0.File类
File类表示一个文件或者一个文件夹。

##### 1.相关方法

创建File对象的方法。

* File(File parent, String child)
* File(String pathname)
* File(String child, File parent)
* File(String pathname, int prefixLength)
* File(String parent, String child)
* File(URI uri)

上面的几种方法会返回一个File对象，但是，我们所指定的文件(文件夹)不一定存在。因此 我们需要通过下面方法判断一下。
```
file.exists()
```
有些情况下，文件可能是隐藏文件，我们可以通过这样得知。
```
file.isHidden()
```
或许，你获取的是个文件夹也说不定。
```
file.isDirectory()
```
当是文件夹的时候，我们可以通过list()方法，列出他下面所有的文件名。举个例子。
```
        File file = new File(".");
        String[] list ;
        if (file.isDirectory()){
            list = file.list();
            System.err.println("is dirctory");
            for (String s : list) {
                System.err.println(s);
            }
        }else {
            System.out.println("is file");
            System.out.println(file.getName());
        }
```
当然，我们可以通过listFiles的方法，直接返回File对象数组。

如果是文件的话，我们还可以判断访问权限，诸如，读写、执行。
```
        file.canRead();
        file.canWrite();
        file.canExecute();
```
当然，我们也可以通过对应的set方法去改变权限。
值得注意的是，执行指的是在linux下面。

还有一个get方法可以获得文件的绝对路径、文件大小、空闲大小、父目录等，这里就不啰嗦了。

##### 2.创建，删除等操作
我们可以通过mkDir、mkDirs去创建文件夹。他们的区别如下。
* mkDir只能在指定目录下创建一级文件，也就是说如果是./a/b的情况，目录./a需要存在。
* mkDirs则是创建多级，如上，a不存在则先创建a，在创建b。

那么，怎么创建文件呢。createNewFile()方法就是用来创建文件的。

说了创建，那么我们就得说下删除了。删除有如下两个方法。
* deleteOnExit 如果存在，就啥un出
* delete 删除

我们在删除文件夹的时候要注意了，因为文件夹下面是有文件的，所以我们调用delete，并不会其效果，我们需要现将他下面的文件删除才行，注意了。

##### 3.重命名
我们可以通过rename方法将文件重命名。

##### 4.RandomAccessFile
RandomAccessFile是一种特使的File,只能是已存在的文件。
获取方法。
```
RandomAccessFile randomAccessFile = new RandomAccessFile("./1.txt","rw");
```
第一个参数表示文件路径，第二个参数表示权限，rw为读写。当只有r权限的时候，如果我们想通过write方法写入内容，就会报EOF描述符异常。
* seek 移动到文件某处
* readxxx 读方法
* writexx 写方法。

##### 5.FileDescriptor 文件描述符






