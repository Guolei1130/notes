#### OkHttp 源码解析（二）

##### 1、分发器-Dispatcher

##### 2、主机验证-OkHostnameVerifier
在握手期间，如果URL的主机名和服务器的标识主机名不匹配，则验证机制可以毁掉此接口的实现程序来确定是否应该允许此链接。

##### 3、证书锁定-CertificatePinner


##### 4、认证-Authenticator


##### 5、连接池-ConnectionPool





_ _ _ 


#### TLS（Transport Layer Security）
安全传输层协议（TLS）用于在两个通信应用程序之间提供保密性和数据完整性。该协议由两层组成：TLS记录协议（TLS Record）和TLS握手协议（TLS Handshake）。

* TLS记录协议是一种分层协议。每一层中的信息可能包含长度、描述和内容等字段。记录协议支持信息传输，将数据分段到可处理块、压缩数据、应用MAC、加密以及传输结果等。对接收到的数据进行解密、校验、解压缩、重组等，然后将他们传送到高层客户机。
* TLS连接状态指的是TLS记录协议的操作环境，他规定看压缩算法、加密算法和MAC算法。


_ _ _ 


 * RealCall 真正的请求
  * enqueue方法中，加锁判断，保证一个请求只发送一次
  * 用okhttpclient的分发器插入异步请求AsyncCall（RealCall的内部类，有一系列方法可以获取origianlRequest的属性（原始请求））。
 * Diapatcher分发器
  * 内部维护这一个线程池，一个同步请求队列、异步请求队列和一个等待队列。
  * ThreadpoolExecutor 最大线程数MaxValue，空闲时存活时间60s
  * 最多并发请求64，最大host5个
  * enqueue方法中判断
    - 如果正在运行的异步队列siz小于最大请求64并且host地址小于5个，就加入执行队列并执行，这是会调用AsyncCall#execute方法。
    - 加入等待队列
  * finished->promoteCalls
    - 如果正在执行队列数大于请求书 直接返回
    - 如果准备队列为空，返回
    - 从准备队列取出请求，加入执行队列病执行，知道执行队列满为止 

 * AsyncCall#execute()
  * getResponseWithInterceptorChain返回请求结果(这是在子线程中) 
  * 根据是否取消请求返回不同的结果
  * 并且调用Dispatcher来finish。

 * getResponseWithInterceptorChain
  * 调用ApplicationInterceptorChain#proceed方法获取Response。 

	```

    @Override public Response proceed(Request request) throws IOException {
      // If there's another interceptor in the chain, call that.
      if (index < client.interceptors().size()) {
        Interceptor.Chain chain = new ApplicationInterceptorChain(index + 1, request, forWebSocket);
        Interceptor interceptor = client.interceptors().get(index);
        Response interceptedResponse = interceptor.intercept(chain);

        if (interceptedResponse == null) {
          throw new NullPointerException("application interceptor " + interceptor
              + " returned null");
        }

        return interceptedResponse;
      }

      // No more interceptors. Do HTTP.
      return getResponse(request, forWebSocket);
    }
  }
```
这里的设计将所有的拦截器串起来了最终的调用还是getResponse方法，

engine.releaseStreamAllocation->streamAllocation.release->deallocate----->解除分配
 * release(connection)
 * Util.closeQuietly(connectionToClose.socket());























