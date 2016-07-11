#### okhttp源码解析-Cache相关

从HttpEngine#sendRequest说起。在这个方法中，有这么一段代码。
```
    InternalCache responseCache = Internal.instance.internalCache(client);
    Response cacheCandidate = responseCache != null
        ? responseCache.get(request)
        : null;

    long now = System.currentTimeMillis();
    cacheStrategy = new CacheStrategy.Factory(now, request, cacheCandidate).get();
    networkRequest = cacheStrategy.networkRequest;
    cacheResponse = cacheStrategy.cacheResponse;
```
```
InternalCache responseCache = Internal.instance.internalCache(client);
```
abstract是一个抽象类，instance是抽象类的静态成员变量，那么，这个成员变量的初始化过程在哪里呢。这个的初始化过程在okhttpclient的静态代码块中。而internalCache方法如下：
```
      @Override public InternalCache internalCache(OkHttpClient client) {
        return client.internalCache();
      }
```
```
  InternalCache internalCache() {
    return cache != null ? cache.internalCache : internalCache;
  }
```
如果有cache，则返回cache的internalCache，否则返回internalCache，我们这里是有cache的，所以返回。而这里的chche是我们传进来的，因此我们需要看下cache的internalCache字段，对应的代码片段如下
```
  final InternalCache internalCache = new InternalCache() {
    @Override public Response get(Request request) throws IOException {
      return Cache.this.get(request);
    }

    @Override public CacheRequest put(Response response) throws IOException {
      return Cache.this.put(response);
    }

    @Override public void remove(Request request) throws IOException {
      Cache.this.remove(request);
    }

    @Override public void update(Response cached, Response network) throws IOException {
      Cache.this.update(cached, network);
    }

    @Override public void trackConditionalCacheHit() {
      Cache.this.trackConditionalCacheHit();
    }

    @Override public void trackResponse(CacheStrategy cacheStrategy) {
      Cache.this.trackResponse(cacheStrategy);
    }
  };
```
回到我们的sendRequest方法中，现在我们知道了sendRequest方法中的responseCache就是Cache中的InternalCache，因此我们继续看下面的代码。
```
    Response cacheCandidate = responseCache != null
        ? responseCache.get(request)
        : null;
```
这里调用get去获取缓存中的Response。我们看下获取缓存的相关逻辑。代码如下。
```
  Response get(Request request) {
    String key = urlToKey(request);
    DiskLruCache.Snapshot snapshot;
    Entry entry;
    try {
      snapshot = cache.get(key);
      if (snapshot == null) {
        return null;
      }
    } catch (IOException e) {
      // Give up because the cache cannot be read.
      return null;
    }

    try {
      entry = new Entry(snapshot.getSource(ENTRY_METADATA));
    } catch (IOException e) {
      Util.closeQuietly(snapshot);
      return null;
    }

    Response response = entry.response(snapshot);

    if (!entry.matches(request, response)) {
      Util.closeQuietly(response.body());
      return null;
    }

    return response;
  }
```
* 获取请求url的md5值
* 通过key获取DiskLruCache的Snapshot对象
* new Entry
* 生成Response对象
* 判断request和response是否匹配(地址，请求方式、请求头)
 * 不匹配 返回null
 * 匹配 返回response

这样，如果缓存中有的话cacheCandidate就是缓存的结果。
```
    long now = System.currentTimeMillis();
    cacheStrategy = new CacheStrategy.Factory(now, request, cacheCandidate).get();
    networkRequest = cacheStrategy.networkRequest;
    cacheResponse = cacheStrategy.cacheResponse;
```
在CacheStrategy.Factory中，更改如下
* 当前事前
* 请求
* chacheResponse
* 发送请求的时间
* 收到响应的时间
* 服务日期 Date
* 协议 Expires
* 上一次修改时间 Last-Modified
* Etag
* Age 到现在为止的时间

```
    if (responseCache != null) {
      responseCache.trackResponse(cacheStrategy);
    }
```
如果缓存不为null，就掉用Cache的trackResponse方法。
```
  private synchronized void trackResponse(CacheStrategy cacheStrategy) {
    requestCount++;

    if (cacheStrategy.networkRequest != null) {
      // If this is a conditional request, we'll increment hitCount if/when it hits.
      networkCount++;
    } else if (cacheStrategy.cacheResponse != null) {
      // This response uses the cache and not the network. That's a cache hit.
      hitCount++;
    }
  }
```
* 请求次数+1
* 如果网络请求不是null +1
* 如果缓存Response不为null +1

接下来我们需要看下readResponse相关方法。
```
    if (cacheResponse != null) {
      if (validate(cacheResponse, networkResponse)) {
        userResponse = cacheResponse.newBuilder()
            .request(userRequest)
            .priorResponse(stripBody(priorResponse))
            .headers(combine(cacheResponse.headers(), networkResponse.headers()))
            .cacheResponse(stripBody(cacheResponse))
            .networkResponse(stripBody(networkResponse))
            .build();
        networkResponse.body().close();
        releaseStreamAllocation();

        // Update the cache after combining headers but before stripping the
        // Content-Encoding header (as performed by initContentStream()).
        InternalCache responseCache = Internal.instance.internalCache(client);
        responseCache.trackConditionalCacheHit();
        responseCache.update(cacheResponse, userResponse);
        userResponse = unzip(userResponse);
        return;
      } else {
        closeQuietly(cacheResponse.body());
      }
```
如果有缓存的话，会根据条件去选择对应的策略，那么是什么条件呢。关键在于validate(cacheResponse, networkResponse)。我们看下这里的逻辑。核心判断如下
```
    Date lastModified = cached.headers().getDate("Last-Modified");
    if (lastModified != null) {
      Date networkLastModified = network.headers().getDate("Last-Modified");
      if (networkLastModified != null
          && networkLastModified.getTime() < lastModified.getTime()) {
        return true;
      }
    }
```
这里的意思是我们控制的cache时间，如果没有超过cache的时间，就返回true，否则，返回false。
如果在缓存的时间内，就启用缓存。

_ _ _ 
那么，我们的缓存是什么时候写入的呢。在readResponse的最下面，有如下代码。
```
    if (hasBody(userResponse)) {
      maybeCache();
      userResponse = unzip(cacheWritingResponse(storeRequest, userResponse));
    }
```
ps:如果能走到这里，说明，没有启用缓存。我们看下maybeCache方法，
```
  private void maybeCache() throws IOException {
    InternalCache responseCache = Internal.instance.internalCache(client);
    if (responseCache == null) return;

    // Should we cache this response for this request?
    if (!CacheStrategy.isCacheable(userResponse, networkRequest)) {
      if (HttpMethod.invalidatesCache(networkRequest.method())) {
        try {
          responseCache.remove(networkRequest);
        } catch (IOException ignored) {
          // The cache cannot be written.
        }
      }
      return;
    }

    // Offer this request to the cache.
    storeRequest = responseCache.put(userResponse);
  }
```
 * 判断是够可以缓存 不可以缓存的话讲对应的删除
 * 加入缓存

看下加入缓存的相关逻辑
```
  private CacheRequest put(Response response) throws IOException {
    String requestMethod = response.request().method();

    if (HttpMethod.invalidatesCache(response.request().method())) {
      try {
        remove(response.request());
      } catch (IOException ignored) {
        // The cache cannot be written.
      }
      return null;
    }
    if (!requestMethod.equals("GET")) {
      // Don't cache non-GET responses. We're technically allowed to cache
      // HEAD requests and some POST requests, but the complexity of doing
      // so is high and the benefit is low.
      return null;
    }

    if (OkHeaders.hasVaryAll(response)) {
      return null;
    }

    Entry entry = new Entry(response);
    DiskLruCache.Editor editor = null;
    try {
      editor = cache.edit(urlToKey(response.request()));
      if (editor == null) {
        return null;
      }
      entry.writeTo(editor);
      return new CacheRequestImpl(editor);
    } catch (IOException e) {
      abortQuietly(editor);
      return null;
    }
  }
```
额，貌似也没啥好看的。






