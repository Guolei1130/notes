从HTTPEngine的readNetworkResponse方法说起，这个方法就是读取响应的。
```
 private Response readNetworkResponse() throws IOException {
    httpStream.finishRequest();

    Response networkResponse = httpStream.readResponseHeaders()
        .request(networkRequest)
        .handshake(streamAllocation.connection().handshake())
        .sentRequestAtMillis(sentRequestMillis)
        .receivedResponseAtMillis(System.currentTimeMillis())
        .build();

    if (!forWebSocket) {
      networkResponse = networkResponse.newBuilder()
          .body(httpStream.openResponseBody(networkResponse))
          .build();
    }

    if ("close".equalsIgnoreCase(networkResponse.request().header("Connection"))
        || "close".equalsIgnoreCase(networkResponse.header("Connection"))) {
      streamAllocation.noNewStreams();
    }

    return networkResponse;
  }
```
#### Http1xStream读取返回结果流程

Http1xStream#openResponse
```
  @Override public ResponseBody openResponseBody(Response response) throws IOException {
    Source source = getTransferStream(response);
    return new RealResponseBody(response.headers(), Okio.buffer(source));
  }
```
重点在于getTransferStream,在这个方法中，通过层层调用，包装了一个Source对象返回。

Removes up to sink.length bytes from this and copies them into sink.
从source中删除sink的长度，并将他们复制到sink中。

#### Http2xStream读取返回结果流程

Http2xStream#openResponseBody
```
  @Override public ResponseBody openResponseBody(Response response) throws IOException {
    Source source = new StreamFinishingSource(stream.getSource());
    return new RealResponseBody(response.headers(), Okio.buffer(source));
  }

```
看到，关键点在于stream.getSource，而这个stream是什么呢？这是一个FramedStrem对象，并且是在writeRequestHeaders方法中初始化的，
```
stream = framedConnection.newStream(requestHeaders, permitsRequestBody, hasResponseBody);
```
而framedConnection是在HttpStrem#newStream方法中初始化病传入的
```
resultStream = new Http2xStream(this, resultConnection.framedConnection);
```
而resultConnection.framedConnection则是RealConnection的framedConnection了，这个对象是在什么时候初始化的呢？是在建立连接的时候，在establishProtocol方法中。
```
    if (protocol == Protocol.SPDY_3 || protocol == Protocol.HTTP_2) {
      socket.setSoTimeout(0); // Framed connection timeouts are set per-stream.

      FramedConnection framedConnection = new FramedConnection.Builder(true)
          .socket(socket, route.address().url().host(), source, sink)
          .protocol(protocol)
          .listener(this)
          .build();
      framedConnection.start();

      // Only assign the framed connection once the preface has been sent successfully.
      this.allocationLimit = framedConnection.maxConcurrentStreams();
      this.framedConnection = framedConnection;
    } else {
      this.allocationLimit = 1;
    }
```
注意，上面讲socket，source，sink都传了进去。
现实new了一个对象，病start，并且把这个对象传给RealConnection的framedConnection。因此，我们看下start方法干了什么，
```
 new Thread(readerRunnable).start();
```
```
readerRunnable = new Reader(variant.newReader(builder.source, client));
```

这里的Reader是一个Runnable是一个实现NamedRunnable的对象没我们需要看下他的execute方法。
```
    @Override protected void execute() {
      ErrorCode connectionErrorCode = ErrorCode.INTERNAL_ERROR;
      ErrorCode streamErrorCode = ErrorCode.INTERNAL_ERROR;
      try {
        if (!client) {
          frameReader.readConnectionPreface();
        }
        while (frameReader.nextFrame(this)) {
        }
        connectionErrorCode = ErrorCode.NO_ERROR;
        streamErrorCode = ErrorCode.CANCEL;
      } catch (IOException e) {
        connectionErrorCode = ErrorCode.PROTOCOL_ERROR;
        streamErrorCode = ErrorCode.PROTOCOL_ERROR;
      } finally {
        try {
          close(connectionErrorCode, streamErrorCode);
        } catch (IOException ignored) {
        }
        Util.closeQuietly(frameReader);
      }
    }

```
可以在while里看到frameReader.nextFrame(this)，而这个framewReader是Http2的Reader对象，我们看下他的方法。

```
variant = new Http2();
```
```
    Reader(BufferedSource source, int headerTableSize, boolean client) {
      this.source = source;
      this.client = client;
      this.continuation = new ContinuationSource(this.source);
      this.hpackReader = new Hpack.Reader(headerTableSize, continuation);
    }
```
额，在nextFrame方法里，我们就能看到读取数据的过程了。

