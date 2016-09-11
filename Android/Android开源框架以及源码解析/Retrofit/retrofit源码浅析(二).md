
接着上一篇，并补充上一篇剩下的几个方法。

### 1.Retrofit#callAdapter

调用nextCallAdapter方法，我们来看下具体逻辑。

* 因为skipPast为null，所以start为 -1 + 1 = 0
* 遍历adapterFactories，如果adalter ！= null，返回(ps:defaultCallAdapterFactory的存在)

### 2.Retrofit#responseBodyConverter

内部调用nextResponseBodyConverter，逻辑和上面一样，在这里就不做描述了。

### 3.OKhttpCall

回到Create方法里面。发现用用serviceMethod和参数args生成了一个OkHttpCall对象。这个对象使用来执行请求了，这里不做过多描述。

### 4.Create返回什么了呢？

```
return serviceMethod.callAdapter.adapt(okHttpCall);
```

yiDeaultCallAdapterFactory来看，就是返回了Call对象，当然，RxJavAdapterFActory一样了。

### 5.Call.enqueue方法。

黑，这里调用的可就是OkHttpCall对象的enqueue方法了。注意注意，这里的OKhttpCALL里面有OKHTTP3.CALL对象化。

在这个方法里，createRawCall对象，并调用这个对象的enqueue执行请求。
### 6.createRawCall
```
  private okhttp3.Call createRawCall() throws IOException {
    Request request = serviceMethod.toRequest(args);
    okhttp3.Call call = serviceMethod.callFactory.newCall(request);
    if (call == null) {
      throw new NullPointerException("Call.Factory returned null.");
    }
    return call;
  }
```
亚哈。我们还需要去看下toRequest方法。

### 7.toRequest
```
 Request toRequest(Object... args) throws IOException {
    RequestBuilder requestBuilder = new RequestBuilder(httpMethod, baseUrl, relativeUrl, headers,
        contentType, hasBody, isFormEncoded, isMultipart);

    @SuppressWarnings("unchecked") // It is an error to invoke a method with the wrong arg types.
    ParameterHandler<Object>[] handlers = (ParameterHandler<Object>[]) parameterHandlers;

    int argumentCount = args != null ? args.length : 0;
    if (argumentCount != handlers.length) {
      throw new IllegalArgumentException("Argument count (" + argumentCount
          + ") doesn't match expected count (" + handlers.length + ")");
    }

    for (int p = 0; p < argumentCount; p++) {
      handlers[p].apply(requestBuilder, args[p]);
    }

    return requestBuilder.build();
  }
```

在这里，就是构建Request对象。具体的实现在这里就不说了。随后在把几个java文件都分析下。

在回去OkHttpCall

### 8. parseResponse

解析OkHttp的返回Response。

* 得到ResponseBody
* build一个rawResponse
* code小于200或者大于等于300，返回Error
* code 为204 或者205，调用Response.success
* 等等


















