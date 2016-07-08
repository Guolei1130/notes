#### OkHttp 源码浅析(-) 整体流程
 * dispatcher 调度器
 * protocls 支持的协议，默认的为http1.1，http2何spdy
 * connectionSpecs 安全连接配置 包括TSL和加密套接词
 * ProxySelector 代理服务器
 * cookieJar 管理cookie
 * socketFactory socket
 * hostnameVerifier 主机验证
 * CertificatePinner ？？？？ 服务器认证
 * proxyAuthenticator 代理验证
 * authenticator 验证
 * connectionPool 连接池
 * dns dns
 * followSslRedirects 是否允许ssl重定向
 * followRedirects 是够允许重定向
 * retryOnConnectionFailure 是否支持连接失败重连
 * connectTimeout 连接超时时间
 * readTimeout 读取超时时间
 * writeTimeout 写入超时时间

#### 请求流程
okhttpclient.newCall(Request).enqueue(Callback callback)
```
  @Override public Call newCall(Request request) {
    return new RealCall(this, request);
  }
```
```
  @Override public void enqueue(Callback responseCallback) {
    enqueue(responseCallback, false);
  }

  void enqueue(Callback responseCallback, boolean forWebSocket) {
    synchronized (this) {
      if (executed) throw new IllegalStateException("Already Executed");
      executed = true;
    }
    client.dispatcher().enqueue(new AsyncCall(responseCallback, forWebSocket));
  }
```

okhttpclient的分发器插入请求队列。并将callback对象分装了一下。
关键点就是dispatcher分发器了。
Dispatcher中有一个线程池，并维护这3个队列，异步请求队列、同步请求队列以及等待队列。
```
synchronized void enqueue(AsyncCall call) {
    if (runningAsyncCalls.size() < maxRequests && runningCallsForHost(call) < maxRequestsPerHost) {
      runningAsyncCalls.add(call);
      executorService().execute(call);
    } else {
      readyAsyncCalls.add(call);
    }
  }
```
在这个方法之中，先判断请求队列和host地址是不是达到限定值，如果达到就载入等待队列，否则就加入运行中的队列并执行。接下来就会掉用call的run方法去发起网络请求并返回数据。因为这里的AsyncCall继承自NamedRunnable，我们来看下NamedRunnable类的run方法，
```

  @Override public final void run() {
    String oldName = Thread.currentThread().getName();
    Thread.currentThread().setName(name);
    try {
      execute();
    } finally {
      Thread.currentThread().setName(oldName);
    }
  }

  protected abstract void execute();
```
可以看到，早run方法中，调用了execute方法。这个方法是在AsyncCall中实现的。那么我们就来看是如何发起请求并获取数据的。
```
Response response = getResponseWithInterceptorChain(forWebSocket);
```
```
  private Response getResponseWithInterceptorChain(boolean forWebSocket) throws IOException {
    Interceptor.Chain chain = new ApplicationInterceptorChain(0, originalRequest, forWebSocket);
    return chain.proceed(originalRequest);
  }

```
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

继续调用getResponse方法，这个方法代码比较成，分段来说。
```
if (body != null) {
      Request.Builder requestBuilder = request.newBuilder();

      MediaType contentType = body.contentType();
      if (contentType != null) {
        requestBuilder.header("Content-Type", contentType.toString());
      }

      long contentLength = body.contentLength();
      if (contentLength != -1) {
        requestBuilder.header("Content-Length", Long.toString(contentLength));
        requestBuilder.removeHeader("Transfer-Encoding");
      } else {
        requestBuilder.header("Transfer-Encoding", "chunked");
        requestBuilder.removeHeader("Content-Length");
      }

      request = requestBuilder.build();
    }
```
这一段是根据请求类型和长度来设置请求头。
```
engine = new HttpEngine(client, request, false, false, forWebSocket, null, null, null);
```
这个HttpEngine才是重点，发请求，读响应都是靠他来的。我们先看发送请求部分。sendRequest方法。代码还是很长，我们挑重点来说。
```
httpStream = connect();
```
没错，这句话很重要，我们跟踪进去看看，一直跟踪到StreamAllocation#newStream方法中。
```
      RealConnection resultConnection = findHealthyConnection(connectTimeout, readTimeout,
          writeTimeout, connectionRetryEnabled, doExtensiveHealthChecks);

      HttpStream resultStream;
      if (resultConnection.framedConnection != null) {
        resultStream = new Http2xStream(this, resultConnection.framedConnection);
      } else {
        resultConnection.socket().setSoTimeout(readTimeout);
        resultConnection.source.timeout().timeout(readTimeout, MILLISECONDS);
        resultConnection.sink.timeout().timeout(writeTimeout, MILLISECONDS);
        resultStream = new Http1xStream(this, resultConnection.source, resultConnection.sink);
      }
```
在这里你就会返现，这里全都是拿socket去实现的。哎，我还是太年轻了。
我们返回HttpEngine中，接下来会调用httpStream.writeRequestHeaders(networkRequest);方法去写入请求头。
```
    stream = framedConnection.newStream(requestHeaders, permitsRequestBody, hasResponseBody);
```
我们跟踪newStream方法看看。会发现有个frameWriter的方法，我们猜测这个就是写入的操作类。而他的初始化时这样的
```
frameWriter = variant.newWriter(builder.sink, client);
```
variantshi http2的实例，因此，得进去http2 一窥究竟。
在前面，我们发现了一个synStream的方法，根据我们的编程经验，这个方法应该有什么东西，因此我们重点看这个方法，
```
    @Override public synchronized void synStream(boolean outFinished, boolean inFinished,
        int streamId, int associatedStreamId, List<Header> headerBlock)
        throws IOException {
      if (inFinished) throw new UnsupportedOperationException();
      if (closed) throw new IOException("closed");
      headers(outFinished, streamId, headerBlock);
    }
```
恩，看到没，有一个headers方法。我们进去看看，
```
hpackWriter.writeHeaders(headerBlock);
```
恩，我们发现上面的这个方法，因此猜测这个才是真正的写入过程。而hpackWriter是Hpack的一个实例。因此，你懂得。
```
    void writeHeaders(List<Header> headerBlock) throws IOException {
      // TODO: implement index tracking
      for (int i = 0, size = headerBlock.size(); i < size; i++) {
        ByteString name = headerBlock.get(i).name.toAsciiLowercase();
        Integer staticIndex = NAME_TO_FIRST_INDEX.get(name);
        if (staticIndex != null) {
          // Literal Header Field without Indexing - Indexed Name.
          writeInt(staticIndex + 1, PREFIX_4_BITS, 0);
          writeByteString(headerBlock.get(i).value);
        } else {
          out.writeByte(0x00); // Literal Header without Indexing - New Name.
          writeByteString(name);
          writeByteString(headerBlock.get(i).value);
        }
      }
    }
```
啊，有种拨开云雾的感觉了，看到，这里将这些请求头写到了一个buffer里面，我们不管具体的写入细节。到现在为止，我们知道了请求头的写入过程。这里用到的是hpack请求头压缩算法。到现在为止，请求头的写入过程明了了。

### 捋一捋Socket的建立过程

在HttpEngin的sendRequest方法中，有下面这一句
```
 httpStream = connect();
```
继续跟踪之后，我们到了StreamAllocation方法中，在这个方法中，我们发现有个叫RealConnection的类，我们猜测，这才是真正创建socket连接的类。一路跟踪之后，我们来到了findConnection方法。并发现有下面这一段代码。
```
 newConnection.connect(connectTimeout, readTimeout, writeTimeout, address.connectionSpecs(),
        connectionRetryEnabled);
```
从调用上来看，猜测这里就是连接的入口，因此，我们打进去看看。而在connect方法之中，
```
        if (route.requiresTunnel()) {
          buildTunneledConnection(connectTimeout, readTimeout, writeTimeout,
              connectionSpecSelector);
        } else {
          buildConnection(connectTimeout, readTimeout, writeTimeout, connectionSpecSelector);
        }
```
我们继续？跟进去
```
  private void buildConnection(int connectTimeout, int readTimeout, int writeTimeout,
      ConnectionSpecSelector connectionSpecSelector) throws IOException {
    connectSocket(connectTimeout, readTimeout, writeTimeout, connectionSpecSelector);
    establishProtocol(readTimeout, writeTimeout, connectionSpecSelector);
  }
```
fuck，还有完没完了？继续跟，
```

    rawSocket = proxy.type() == Proxy.Type.DIRECT || proxy.type() == Proxy.Type.HTTP
        ? address.socketFactory().createSocket()
        : new Socket(proxy);
```
额，看见了createSocket()、
好吧，这里我们看到，这里有address，恩，我们要理解下，so，我们去看下Address类。因为是通过socketFactory来创建socket连接的，那么，我们就需要知道SocketFactory是什么鬼，因此，我们通过构造函数来查找谁调用链他，发现，我们又回到了HttpEngine类，这次是createAddress方法，发现socketFactory是okhttpclient的。好，我们看这个。
```
socketFactory = SocketFactory.getDefault();
```
最后，我们发现
```
    public static synchronized SocketFactory getDefault() {
        if (defaultFactory == null) {
            defaultFactory = new DefaultSocketFactory();
        }
        return defaultFactory;
    }

```
，到这里我们就知道了socket的创建过程了。但是还没完，回到RealConnection#connectSocket方法。
```
Platform.get().connectSocket(rawSocket, route.socketAddress(), connectTimeout);
```
最后就追踪到了。
```
socket.connect(address, connectTimeout);
```

#### 读取返回结果

HttpEngine#readResponse方法中调用readNetWorkResponse方法去读取相应结果。
```
    Response networkResponse = httpStream.readResponseHeaders()
        .request(networkRequest)
        .handshake(streamAllocation.connection().handshake())
        .sentRequestAtMillis(sentRequestMillis)
        .receivedResponseAtMillis(System.currentTimeMillis())
        .build();
```
,额，看起来有点乱，我们看readResponseHeaders方法，这里的httpStream，我们以http2x，为例，
```

  @Override public Response.Builder readResponseHeaders() throws IOException {
    return framedConnection.getProtocol() == Protocol.HTTP_2
        ? readHttp2HeadersList(stream.getResponseHeaders())
        : readSpdy3HeadersList(stream.getResponseHeaders());
  }
```
这里通过判断，去判断是不是HTTP2协议，我们看readHttp2HeadersList方法，关键代码如下
```
    StatusLine statusLine = StatusLine.parse("HTTP/1.1 " + status);
    return new Response.Builder()
        .protocol(Protocol.HTTP_2)
        .code(statusLine.code)
        .message(statusLine.message)
        .headers(headersBuilder.build());
```
这个statusline是什么东西呢？这是返回的状态信息。我们不管，然后我们回到最初的地方
```
networkResponse = networkResponse.newBuilder()
          .body(httpStream.openResponseBody(networkResponse))
          .build();
```
发现这里会包装下，我们看http2x的openResponseBody方法。
```
  @Override public ResponseBody openResponseBody(Response response) throws IOException {
    Source source = new StreamFinishingSource(stream.getSource());
    return new RealResponseBody(response.headers(), Okio.buffer(source));
  }
```
可以看到的是，这里用到了FramedDataSource，这是source的一个子类。而RealResponseBody则是ResponseBody的一个子类，因此我们就知道了数据是怎么包装了的。但是数据从哪里来的呢？原因就在于stream，而这个stream是我们在写入请求头的时候初始化的。
```
 stream = framedConnection.newStream(requestHeaders, permitsRequestBody, hasResponseBody);
```
通过层层调用到达了FramedStream类中。
```
  FramedStream(int id, FramedConnection connection, boolean outFinished, boolean inFinished,
      List<Header> requestHeaders) {
    if (connection == null) throw new NullPointerException("connection == null");
    if (requestHeaders == null) throw new NullPointerException("requestHeaders == null");
    this.id = id;
    this.connection = connection;
    this.bytesLeftInWriteWindow =
        connection.peerSettings.getInitialWindowSize(DEFAULT_INITIAL_WINDOW_SIZE);
    this.source = new FramedDataSource(
        connection.okHttpSettings.getInitialWindowSize(DEFAULT_INITIAL_WINDOW_SIZE));
    this.sink = new FramedDataSink();
    this.source.finished = inFinished;
    this.sink.finished = outFinished;
    this.requestHeaders = requestHeaders;
  }
```
这里，我们知道了数据是存在哪里，但是我们还是不知道数据是怎么写入进来的。

#### 1222
在RealConnection的connet方法中，有buildConnection方法，跟踪这个方法
```
  private void buildConnection(int connectTimeout, int readTimeout, int writeTimeout,
      ConnectionSpecSelector connectionSpecSelector) throws IOException {
    connectSocket(connectTimeout, readTimeout, writeTimeout, connectionSpecSelector);
    establishProtocol(readTimeout, writeTimeout, connectionSpecSelector);
  }
```
除了连接socket之外，还建立连接协议。我们看看建立廉洁协议的实现。
```
FramedConnection framedConnection = new FramedConnection.Builder(true)
          .socket(socket, route.address().url().host(), source, sink)
          .protocol(protocol)
          .listener(this)
          .build();
      framedConnection.start();
```
我们重点看start方法，
```
  void start(boolean sendConnectionPreface) throws IOException {
    if (sendConnectionPreface) {
      frameWriter.connectionPreface();
      frameWriter.settings(okHttpSettings);
      int windowSize = okHttpSettings.getInitialWindowSize(Settings.DEFAULT_INITIAL_WINDOW_SIZE);
      if (windowSize != Settings.DEFAULT_INITIAL_WINDOW_SIZE) {
        frameWriter.windowUpdate(0, windowSize - Settings.DEFAULT_INITIAL_WINDOW_SIZE);
      }
    }
    new Thread(readerRunnable).start(); // Not a daemon thread.
  }
```
可以看到，有一个readerRunnable的Runnable，我们猜测这个是读取socket连接从数据的。因此，我们看下他的实现，发现是一个Reader对象。我们看他的execute方法。
```
if (!client) {
          frameReader.readConnectionPreface();
        }
        while (frameReader.nextFrame(this)) {
        }
```

_ _ _ 
我们看http1stream的构造函数
```
  public Http1xStream(StreamAllocation streamAllocation, BufferedSource source, BufferedSink sink) {
    this.streamAllocation = streamAllocation;
    this.source = source;
    this.sink = sink;
  }
```
将BufferedSource和BufferedSink传了进去。
而这2个东西。
```
    source = Okio.buffer(Okio.source(rawSocket));
    sink = Okio.buffer(Okio.sink(rawSocket));
```
这是获取socket的输入流和输出流的。
