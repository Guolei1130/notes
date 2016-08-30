#### okhttp源码浅析-失败重连
在RealCall#getResponse中
假如在请求过程中出现RequestException、RouteException或者IOException，都会执行下面代码。
```
        HttpEngine retryEngine = engine.recover(e, false, null);
        if (retryEngine != null) {
          releaseConnection = false;
          engine = retryEngine;
          continue;
        }

```
上面代码的关键在于engine.recover方法，这个方法会返回一个retryEngine，并从新发送请求、读取相应。
```
  public HttpEngine recover(IOException e, boolean routeException, Sink requestBodyOut) {
    streamAllocation.streamFailed(e);

    if (!client.retryOnConnectionFailure()) {
      return null; // The application layer has forbidden retries.
    }

    if (requestBodyOut != null && !(requestBodyOut instanceof RetryableSink)) {
      return null; // The body on this request cannot be retried.
    }

    if (!isRecoverable(e, routeException)) {
      return null; // This exception is fatal.
    }

    if (!streamAllocation.hasMoreRoutes()) {
      return null; // No more routes to attempt.
    }

    StreamAllocation streamAllocation = close();

    // For failure recovery, use the same route selector with a new connection.
    return new HttpEngine(client, userRequest, bufferRequestBody, callerWritesRequestBody,
        forWebSocket, streamAllocation, (RetryableSink) requestBodyOut, priorResponse);
  }
```
* 释放资源
* 如果okhttpclient不允许重连，返回null
* body不能重连，返回null
* routeException不可以解决，返回null
* 没有更多route，返回null
* 返回一个新的HttpEngine对象。