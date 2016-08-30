### Java Nio

[参考资料](http://ifeve.com/java-nio-channel-to-channel/)
[参考资料](http://blog.csdn.net/u013256816/article/details/51457215)
[参考资料](http://tutorials.jenkov.com/java-nio/selectors.html)

nio是非阻塞性的,面向缓冲区的。
nio的目的是为了提高速度，尤其是在文件IO与网络IO中。

### nio中的几个核心概念
通道和缓存器(相当于煤层和运煤车)

* Channels
* Buffers
* Selectors

channel，既可以将Buffer中的数据度渠道channel中，也可以将数据输出到buffer中。

Channel有下面几种类型。

* FileChannel 文件
* DatagramChannel udp
* SocketChannel 通过tcp读取网络中的数据
* ServerSocketChannel 监听新进来的tcp链接，像web服务器那样

Buffer的类型

* ByteBuffer
* CharBuffer
* DoubleBuffer
* FloatBuffer
* IntBuffer
* LongBuffer
* ShortBuffer

Selector

Selector允许单线程处理多个Channel。得向Selector注册Channel，然后调用它的select方法。

_ _ _

### Channel

* 双向的，既可以写入数据，也可以读取数据
* 可以进行异步读写
* 总要先读到一个Buffer，或者总要从一个Buffer中写入

例子

```
        try {
            FileChannel fc = new RandomAccessFile("./1.txt","rw").getChannel();
            ByteBuffer bf = ByteBuffer.allocate(1024);
            //将输入读入buffer
            fc.read(bf);
            bf.flip();
            while (bf.hasRemaining()){
                System.err.println((char) bf.get());
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
```

* flip的作用是翻转，意思就是，如果当前为写入状态，如果我们要读取数据，就需要调用他来翻转一下。
* hasRemaining是判断是否有下一个byte

如果我们当前为read，我们还想进行以下read操作，那么我们需要调用下clear或者compact清除已经读过的数据，没读过的数据将会
被放置在头部方法为每个read做好准备。

我们可以通过transferTo 和 transferFrom方法来链接两个通道

_ _ _

Buffer

Buffer四个索引如下

* mark 标记
* position 位置
* limit 界限
* capacity 容量

* limit和capacity位于末尾，position位于首位
* 调用mark()方法将mark标记设置为position
* 调用buffer的get方法将，mark标记往后移
* put或者reset方法，将position设置为mark的值

写入数据

* read  或 put方法

从buffer中读数据到channel

* channel的write方法，buf的get方法。

rewind方法，将position设置为0

enquals

equals()

当满足下列条件时，表示两个Buffer相等：

    有相同的类型（byte、char、int等）。
    Buffer中剩余的byte、char等的个数相等。
    Buffer中所有剩余的byte、char等都相同。


compareTo()方法

compareTo()方法比较两个Buffer的剩余元素(byte、char等)， 如果满足下列条件，则认为一个Buffer“小于”另一个Buffer：

    第一个不相等的元素小于另一个Buffer中对应的元素 。
    所有元素都相等，但第一个Buffer比另一个先耗尽(第一个Buffer的元素个数比另一个少)。

* capacoty() 返回缓冲区容量
* clear() 清空缓冲区，position设置0，limit设为容量
* flip() limit设为position，position设置为0，
* remaining() 返回limit-position
* hasRemaining() 若有介于position和limit之间的

_ _ _

### Scatter/Gather

Scatter,从一个Channel中读取的数据分散到n个缓冲区
Gather，将N个Buffer里的内容按照顺序发送到一个Channel里。

read(ByteBuffer[] dsts)
write(ByteBuffer[] srcs)

###  通道之间的数据传输

transforFrom可以将数据从源通道传输到目标通道
tramstiForm 一样，只不过就是调用顺序不一样。

### Selector

Selector（选择器）是Java NIO中能够检测一到多个NIO通道，并能够知晓通道是否为诸如读写事件做好准备的组件。
这样，一个单独的线程可以管理多个channel，从而管理多个网络连接。

单个线程管理多个通道

Selector的创建。 open方法。
向Selector注册通道 

channel.configureBlocking(false);
SelectionKey key = channel.register(selector,
Selectionkey.OP_READ);

注意register()方法的第二个参数。这是一个“interest集合”，意思是在通过Selector监听Channel时对什么事件感兴趣。可以用|，多个
可以监听四种不同类型的事件：

* Connect
* Accept
* Read
* Write

Selector注册Channel时，register()方法会返回一个SelectionKey对象。这个对象包含了一些你感兴趣的属性：

    interest集合
    ready集合
    Channel
    Selector
    附加的对象（可选）
    
interest集合是你所选择的感兴趣的事件集合.
interest集合，像这样：
nt interestSet = selectionKey.interestOps();
boolean isInterestedInAccept  = (interestSet & SelectionKey.OP_ACCEPT) == SelectionKey.OP_ACCEPT；
boolean isInterestedInConnect = interestSet & SelectionKey.OP_CONNECT;
boolean isInterestedInRead    = interestSet & SelectionKey.OP_READ;
boolean isInterestedInWrite   = interestSet & SelectionKey.OP_WRITE;

可以看到，用“位与”操作interest 集合和给定的SelectionKey常量，可以确定某个确定的事件是否在interest 集合中。

ready集合
ready 集合是通道已经准备就绪的操作的集合。
Channel + Selector
    Channel  channel  = selectionKey.channel();
    Selector selector = selectionKey.selector();
    
附加的对象

可以将一个对象或者更多信息附着到SelectionKey上，这样就能方便的识别某个给定的通道。例如，可以附加 与通道一起使用的Buffer，或是包含聚集数据的某个对象。使用方法如下：
查看源代码
打印
帮助
electionKey.attach(theObject);
Object attachedObj = selectionKey.attachment();

还可以在用register()方法向Selector注册Channel的时候附加对象。如：
SelectionKey key = channel.register(selector, SelectionKey.OP_READ, theObject);

通过Selector选择通道
select()阻塞到至少有一个通道在你注册的事件上就绪了。

select(long timeout)和select()一样，除了最长会阻塞timeout毫秒(参数)。

selectNow()不会阻塞，不管什么通道就绪都立刻返回
（译者注：此方法执行非阻塞的选择操作。如果自从前一次选择操作后，没有通道变成可选择的，则此方法直接返回零。）。

SelectKeys，调用了select方法之后，返回已选择键集中的就绪通道。

wakeUp() 某个线程调用select()方法后阻塞了，即使没有通道已经就绪，也有办法让其从select()方法返回。
只要让其它线程在第一个线程调用select()方法的那个对象上调用Selector.wakeup()方法即可。阻塞在select()方法上的线程会立马返回。

close  关闭Selector，通道本身不会关闭

完整的实例。

_ _ _ 

### FileChannel 
file.getChannel() 获取channel

分配一个Buffer，ByteBuffer bb = ByteBuffer.allocate(size).

将数据从channel中读到buffer channel.read(byte)

写入数据 channel.write

### SocketChannel

[参考资料](http://ifeve.com/socket-channel/)

### SocketChannelServer

### DatagramChannel

[实现UDP协议传输](http://blog.csdn.net/foart/article/details/47608475)

* 不需要connet建立链接
* 通过send发送数据
* 通过receive接受数据

### Java IO 和 NIO

IO           NIO
面向流         面向缓冲
阻塞IO         非阻塞IO
无             选择器



