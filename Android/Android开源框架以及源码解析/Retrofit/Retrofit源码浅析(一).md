不了解其源码，如何能灵活应用，尤其是Retrofit这类灵活的框架。这一篇先从源头Retrofit.Java类开始。

### 1.Retrofit#Builder
作为一个优秀的框架，建造者模式是少不了的。我们来看下对可以配置那些参数。

* Platform  平台，Java、IOS、Android
* okhttp3.Call.Factory 
* HttpUrl baseUrl
* List<'Converter.Factory'> 一些列的包装器
* List<‘CallAdapter.Factory’> 一系列Adapter
* Executor 
* validateEagerly 

一些方法。

* baseurl(HttpUrl httpurl)
* baseUrl(String url)

设置BaseUrl。关于BaseUrl的格式，这里就不在多说，还是看下Retrofit里的吧。

* addCallAdapterFactory 添加一个Call Adapter Factory为server method的返回值

* addConverterFactory 对object进行序列化与反序列化

* callbackExecutor server method返回的时候将会调用
* callFactory  指定Factory
* client  指定OkhttpClient
* validateEagerly  配置是否在返回的时候立马验证


### 2.Retrofit

一个Retrofit对象宝货一下属性。

额，略。

咱们还是来看下create方法吧。

create返回一个Server对象，嘻嘻。

```
  public <T> T create(final Class<T> service) {
    Utils.validateServiceInterface(service);
    if (validateEagerly) {
      eagerlyValidateMethods(service);
    }
    return (T) Proxy.newProxyInstance(service.getClassLoader(), new Class<?>[] { service },
        new InvocationHandler() {
          private final Platform platform = Platform.get();

          @Override public Object invoke(Object proxy, Method method, Object... args)
              throws Throwable {
            // If the method is a method from Object then defer to normal invocation.
            if (method.getDeclaringClass() == Object.class) {
              return method.invoke(this, args);
            }
            if (platform.isDefaultMethod(method)) {
              return platform.invokeDefaultMethod(method, service, proxy, args);
            }
            ServiceMethod serviceMethod = loadServiceMethod(method);
            OkHttpCall okHttpCall = new OkHttpCall<>(serviceMethod, args);
            return serviceMethod.callAdapter.adapt(okHttpCall);
          }
        });
  }
```

* 进行合法性判断，判断是不是 interface并且interface里的方法大于0个
* 如果是先进性验证的话(validateEagerly = true)
	* 遍历server的所有方法，如果方法不是Class的几个默认方法的话，就进行loadServiceMethod(这个稍后再说)
* 返回代理对象
	* 如果定义method的地方为Object.class，不做处理
    * loadServiceMethod
	* 生成OKhttpCall对象
    * serviceMethod.callAdapter.adapt（okhttpCall）
  
### 3.loadServiceMethod

这个方法构造ServiceMethod对象。

```
    public Builder(Retrofit retrofit, Method method) {
      this.retrofit = retrofit;
      this.method = method;
      this.methodAnnotations = method.getAnnotations();
      this.parameterTypes = method.getGenericParameterTypes();
      this.parameterAnnotationsArray = method.getParameterAnnotations();
    }
```
* retrofit
* method
* method的注解
* method的参数类型
* method的参数的注解

接着来看build方法。

* createCallAdapter
* 指定responseType，(callAdapter.responseType)
* createResponseCOnverter
* parseMethodAnnotation 解析方法的注解
* 指定parameterCount
* ParameterHandler
* 返回ServiceMethod对象。

### 4.ServiceMethod#createCallAdapter
```
    private CallAdapter<?> createCallAdapter() {
      Type returnType = method.getGenericReturnType();
      if (Utils.hasUnresolvableType(returnType)) {
        throw methodError(
            "Method return type must not include a type variable or wildcard: %s", returnType);
      }
      if (returnType == void.class) {
        throw methodError("Service methods cannot return void.");
      }
      Annotation[] annotations = method.getAnnotations();
      try {
        return retrofit.callAdapter(returnType, annotations);
      } catch (RuntimeException e) { // Wide exception range because factories are user code.
        throw methodError(e, "Unable to create call adapter for %s", returnType);
      }
```
* 获取Method的返回类型
* 获取method的多有注解
* 调用Retrofit的CallAdapter方法生成CallAdapter

### 5.createResponseConverter

调用Retrofit的createResponseConverter方法生成Converter对象

### 6.parseMethodAnnotation

这个方法里调用parseHttpMethodAndPath方法解析。

### 7.parseHttpMethodAndPath

* httpmethod
* 是否有body
* 得到正在的url，真正的参数

### 8.parseParameterAnnotation

恩，这里就是我们解析参数的地方。方法很长，但是不难，我们就不多说了。

到这里的话，参数、url就拼接全了。

### 9.servicemethod

* callFactory callFactory(Okhttp3.call.Factory)
* callAdapter 返回类型适配器
* baseUrl baseUrl
* responseConverter 相应的包装器
* httpMethod 请求方式
* realtiveUrl 相对url
* headers 请求头
* contentType 请求类型
* hasBody 是否有请求体
* isFormEncoded 是否是表单
* isMultipart 是否是multipart类型
* parameterHandlers 所有参数






























