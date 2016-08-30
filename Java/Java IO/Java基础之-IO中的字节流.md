下文中所出现的代码可能不规范，譬如文件检查，读者自行体会用法就好。
### 0.什么是字节流
简单来说就是面向字节的IO流，也就是提供的read或者write方法，是对byte或者byte[] 操作的。在Java中，有这么几类。

**InputStream**

| 类        | 功能             | 构造器的参数  |
| ------------- |:-------------:| -----:|
| ByteArrayInputStream      | 将内存中的缓冲区当做InputStream使用 | byte[] |
| StringBufferInputStream | 将String转化为InputStream | String |
|FIleInputStream |从文件中读取信息 | File,FileDescriptor(文件描述符),String(文件)|
|PipedInputStream |读取PipedOutputStream输入的数据，实现“管道化” | PipedOutputStream，需要和PipedOutputStream建立链接 | 
| SequenceInputStream| 将两个或多个流合并成一个（按顺序）|1.两个流 2.充满流的容器 |
|FilterInputStream |包装其他流，提供更多有用的功能 | |

**OutputStream** 
|类名 |功能 |参数 |
|-|:-:|-:|
|ByteOutputStream |将输入写入到byte[] |大小size |
|FileOutputStream|输入到文件 |  File,FileDescriptor(文件描述符),String(文件)|
| PipedOutputStream| 输入数据，当做PipedInputStream的输入数据，实现“管道化” |PipedInputStream，需要和PipedInputStream建立链接关系 |
|FilterOutputStream | 包装其他流，提供更多有用的功能| |

### 1.通用
通过read和write的方法来进行数据的读写。
### 2.ByteArray相关
举个例子，现在将一个byte[]数组的内容读出来并输入到控制台。
```
        String s = new String("这是测试数据");
        byte[] in = new byte[0];
        try {
            in = s.getBytes("utf-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        ByteArrayInputStream bais = new ByteArrayInputStream(in);
        byte[] bytes1 = new byte[1024];
        try {
            StringBuffer stringBuffer = new StringBuffer();
            while (bais.read(bytes1) != -1){
                stringBuffer.append(new String(bytes1).trim());
                System.err.println(stringBuffer);
            }
            bais.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
```
看下控制台的输出。

![这里写图片描述](http://img.blog.csdn.net/20160806115524375)
由于StringBufferInputStream和ByteArrayInputStream的用法一致，就不在多说。
### 2.File相关
举个例子，从一个文件中读取内容，写入到另一个文件中。代码如下。
```
        File file = new File("./1.txt");
        File file1 = new File("./2.txt");
        if (!file1.exists()){
            try {
                file1.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        try {
            FileInputStream fis = new FileInputStream(file);
            StringBuffer sb = new StringBuffer();
            FileOutputStream fos = new FileOutputStream(file1);
            byte[] b = new byte[1024];
            while ( fis.read(b) != -1){
                sb.append(new String(b).trim());
            }
            fos.write(b);
            fos.flush();
            fos.close();
            System.err.println(sb);
            fis.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {

        }
```
### 3.Piped相关
在此之前，我们需要了解一下管道。[点我了解管道](http://blog.chinaunix.net/uid-27034868-id-3394243.html)
接下来，我们以两个线程为例，来展示下他的用法。
```
    static class ReadThread implements Runnable{

        private PipedInputStream pis ;

        public ReadThread(PipedInputStream pis){
            this.pis = pis ;
        }

        @Override
        public void run() {
            byte[] b = new byte[1024];
            try {
                if (null != pis){
                    System.err.println(getDate() + "  等待另一头输入数据");
                    while (pis.read(b) != -1){
                        System.err.println(getDate()+ "  读取成功,数据为:"+new String(b).trim());
                    }
                    pis.close();
                }else {
                    System.err.println("pis is null");
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
```
读线程一直等待输入线程像管道中写入数据。若没有数据，就一致处在阻塞状态。
接下来我们看写入线程。
```
    static class WriteThread implements Runnable{

        private PipedOutputStream pos;

        public WriteThread(PipedOutputStream pos){
            this.pos = pos ;
        }

        @Override
        public void run() {
            byte[] b = new byte[1024];
            try {
                if (null != pos){
                    Thread.sleep(5000);
                    pos.write("这是输入的数据".getBytes("utf-8"));
                    System.err.println(getDate()+"  输入数据成功");
                    pos.close();
                }
            }catch (IOException e){
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
```
接下来我们就看下控制台的输出。稍后我会将这个类push到https://github.com/Guolei1130/ATips/tree/master/Java 下面。
![这里写图片描述](http://img.blog.csdn.net/20160806124516444)

### 4.SequenceInputStream
```
FileInputStream fis_1 = new FileInputStream(new File("./1.txt"));
            FileInputStream fis_2 = new FileInputStream(new File("./2.txt"));

            FileOutputStream fos = new FileOutputStream(new File("./3.txt"));

            //将两个或者多个合并.
            SequenceInputStream sis = new SequenceInputStream(fis_1,fis_2);

            byte[] b = new byte[1024];
            while (sis.read(b) != -1){
                fos.write(b);
            }
            sis.close();
            fos.close();
            fis_1.close();
            fis_2.close();
```
将两个流合并成一个之后，(这里是有顺序的),输出的流就会按照输入流的顺序输出数据。
### 5.Filter相关
这个很重要，因为我们在平常使用过程中往往需要结合这些来用，一般的流只提供了read，write方法，而装饰之后的流则有更多的方法，如DataInputStrem，提供了readInt等，比较方便。还有就是BufferedInputStream使用缓存区，来防止每次都进行实际操作。
**FilterInputStream**
| 类|功能 | 构造器参数| 比普通流增加了那些功能|
|--|:--:|:-:|--:|
|DataInputStream|读取基本数据类型 |InputStream|增加基础数据类型的读写 |
|BufferedInputStream|使用缓存区|- |增加缓存区|
|LineNumberInputStream |跟踪输入流中的行号 |- | 增加行号|
|PushedbackInputStream |能弹出字节的缓冲区 |- | |

**FilterOutputStream**
| 类|功能 | 构造器参数| 比普通流增加了那些功能|
|--|:--:|:-:|--:|
|DataOutputStream|写入基本数据类型 |OutputStream|增加基础数据类型的读写 |
|PrintStream |格式化参数 | -| |
|BufferedOutputStream|使用缓存区|- |增加缓存区|

### 6.总结
这一块虽然简单点，但是还是很重要的。

