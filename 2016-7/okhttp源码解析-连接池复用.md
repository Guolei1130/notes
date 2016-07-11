#### Okhttp源码解析-连接池复用
从StreamAllocation#findHealthyConnection说起。
* 连接超时时间
* 读取超时时间
* 写入超时时间
* 是否允许重连
* 是否健康检查
_ _ _
* 先找到连接
* 判断是不是健康连接
我们先看照连接的过程。
```
      RealConnection candidate = findConnection(connectTimeout, readTimeout, writeTimeout,
          connectionRetryEnabled);
```
看findConnection中相关逻辑。
```
      RealConnection pooledConnection = Internal.instance.get(connectionPool, address, this);
      if (pooledConnection != null) {
        this.connection = pooledConnection;
        return pooledConnection;
      }
```
Internal.instance.get(connectionPool, address, this)方法在okhttpclient的静态代码块里面，我们看ConnectionPool的对应的get方法。
```
  RealConnection get(Address address, StreamAllocation streamAllocation) {
    assert (Thread.holdsLock(this));
    for (RealConnection connection : connections) {
      if (connection.allocations.size() < connection.allocationLimit
          && address.equals(connection.route().address)
          && !connection.noNewStreams) {
        streamAllocation.acquire(connection);
        return connection;
      }
    }
    return null;
  }
```
这样就返回了一个已经存在的同样的RealConnect对象。
streamAllocation.acquire(connection);是RealConnection的allocations增加了一个弱引用。

回到StreamAllocation的findConnection方法，，如果从连接池中拿到了RealConnection对象，就返回。否则，继续。生成一个RealConnection对象，并加入连接池。
现在我们看看健康检查是怎么回事。
在RealConnection#isHealthy中，这里就是检查socket的各项指标是够正确，比如说socket关闭没，输入输出通道关闭没。
如果不健康的话就调用noNewStream，这里就是释放这个RealConnection所占的资源，并且从上面操作，直到返回一个可用的健康的RealConnection。



