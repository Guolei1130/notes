### Retrofit文件上传
当前使用版本为
```
compile 'com.squareup.retrofit2:retrofit:2.0.2'
```
#### 1.API编写
```
public interface uploadfileApi {
    @Multipart
    @POST("/fileabout.php")
    Call<String> upload(@Part("fileName") String des,
                        @Part("file\"; filename=\"1.txt") RequestBody file);
}
```
* @Part("fileName") String des 可以加一些描述信息(可以不加)
* @Part("file\"; filename=\"1.txt") 格式不变，只需将1.text 对应的替换为你想在服务器生成的文件名称
* 如果想传多个文件，对应的加@Part("file\"; filename=\"1.txt") 即可

当然，上面那种写法灵活性太差，我们可以选择这样
```
public interface uploadfileApi {
    @Multipart
    @POST("/fileabout.php")
    Call<String> upload_2(@Part("fileName") String des,
                          @PartMap Map<String,RequestBody> params);
}
```
通过如下方法传入参数
```
Map<String,RequestBody> params = new HashMap<String, RequestBody>();
                params.put("file\"; filename=\""+file.getName()+"", requestBody);

```

#### 2.文件上传
```
Retrofit retrofit= new Retrofit.Builder()
                        .addConverterFactory(GsonConverterFactory.create())
                        .baseUrl("http://192.168.56.1")
                        .build();
                uploadfileApi service = retrofit.create(uploadfileApi.class);
//                Call<Des> model = service.post(new User("guolei", "123456"));
                File file = new File(Environment.getExternalStorageDirectory() + "/" + "1.txt");
                RequestBody requestBody =
                        RequestBody.create(MediaType.parse("multipart/form-data"),file);
                Call<String> model = service.upload("this is txt",requestBody);
                model.enqueue(new Callback<String>() {
                    @Override
                    public void onResponse(Call<String> call, Response<String> response) {
                        Log.e(TAG, "onResponse: " + response.body().toString() );
                    }

                    @Override
                    public void onFailure(Call<String> call, Throwable t) {

                    }
                });
```
* File file = new File(Environment.getExternalStorageDirectory() + "/" + "1.txt"); 要上传的文件
* RequestBody requestBody =                  RequestBody.create(MediaType.parse("multipart/form-data"),file); 固定 类似form表单

那种可定义的只需将下面这行代码替换即可
```
service.upload_2("des",params);
```

#### 3.服务端接收文件
这里以php为例，window操作系统，假设环境装好，服务开启状态。接收代码如下
```
<?php
	//var_dump($_POST);
	//var_dump($_FILES);
	$myfile = fopen("testfile.txt", "w");
	fwrite($myfile, $_FILES["file"]["tmp_name"]."\n"
		."D:\WWW"."\\".$_FILES["file"]["name"]);
	move_uploaded_file($_FILES["file"]["tmp_name"], "D:\WWW"."\\".$_FILES["file"]["name"]);
```
* $myfile = fopen("testfile.txt", "w");
	fwrite($myfile, $_FILES["file"]["tmp_name"]."\n"."D:\WWW"."\\".$_FILES["file"]["name"]); 将信息打印到textfile.txt文件中，(要注意文件路径)
* move_uploaded_file($_FILES["file"]["tmp_name"], "D:\WWW"."\\".$_FILES["file"]["name"]); 将接收到的文件移动到D:\\WWW\文件夹下，文件名为上传时设置的文件名。

_ _ _

### Retrofit文件下载与进度监听
Retrofit并没有给我们提供文件下载进度的相关信息，但是，我们还是可以从一些渠道知道如何监听下载进度，在OKHTTP的官方demo里面有一个Progress.java的文件，从名字上就知道与进度有关。[github地址](https://github.com/square/okhttp/blob/master/samples/guide/src/main/java/okhttp3/recipes/Progress.java)
#### 1.改造ResponseBody
okhttp3默认的responsebody是不能满足我们的要求的，(不能知道进度的相关信息)，我们需要作出改造，首先需要个接口，监听进度信息。其次，好吧，我承认这是废话，我们只需要把Progress.java中我们需要的拿出来就好。
##### 1.1 interface
```
public interface ProgressListener {
    /**
     * @param progress     已经下载或上传字节数
     * @param total        总字节数
     * @param done         是否完成
     */
    void onProgress(long progress, long total, boolean done);
}

```

##### 1.2 ProgressResponseBody
```
public class ProgressResponseBody extends ResponseBody {

    private final ResponseBody responseBody;
    private final ProgressListener listener;
    private BufferedSource bufferedSource;

    public ProgressResponseBody(ResponseBody responseBody,ProgressListener listener){
        this.responseBody = responseBody;
        this.listener = listener;
    }
    @Override
    public MediaType contentType() {
        return responseBody.contentType();
    }

    @Override
    public long contentLength() {
        return responseBody.contentLength();
    }

    @Override
    public BufferedSource source() {
        if (null == bufferedSource){
            bufferedSource = Okio.buffer(source(responseBody.source()));
        }
        return bufferedSource;
    }

    private Source source(Source source) {
        return new ForwardingSource(source) {
            long totalBytesRead = 0L;

            @Override
            public long read(Buffer sink, long byteCount) throws IOException {
                long bytesRead = super.read(sink, byteCount);
                totalBytesRead += bytesRead != -1 ? bytesRead : 0;
                listener.onProgress(totalBytesRead, responseBody.contentLength(), bytesRead == -1);
                return bytesRead;
            }
        };
    }
}
```
恩，就这些，别问我okio的相关知识，我正在学呢。。。

#### 2.Api编写
```
public interface DownLoadApi {
    @GET("files/{fileId}")
    @Headers({"Content-Type: image/*"})
    Call<ResponseBody> getFile(@Path("fileId") String fileId);

}
```
这里的fileId其实我感觉用fileName更合适一点，毕竟我们下载文件是知道文件名的。Content-Type 对应的是文件的MIME类型，设置为你的文件类型即可。

#### 3.使用自己的OkHttpClient
我们需要通过OkHttpClient的拦截器去拦截Response，并将我们的ProgressReponseBody设置进去，这样才能监听进度。那么，我们怎么讲client设置进去呢。通过观察Retrofit的结构发现，Builder下面有client()方法可以设置，好，那么我们通过Retrofit.Builder来创建(这样我们可以配置了)。
截图如下
![](https://github.com/Guolei1130/ATips/blob/master/image/retrofit/retrofit.png)
代码如下
```
Retrofit.Builder builder = new Retrofit.Builder()
                .baseUrl("http://192.168.56.1");
        OkHttpClient client = new OkHttpClient.Builder()
                .addNetworkInterceptor(new Interceptor() {
                    @Override
                    public okhttp3.Response intercept(Chain chain) throws IOException {
                        okhttp3.Response orginalResponse = chain.proceed(chain.request());

                        return orginalResponse.newBuilder()
                                .body(new ProgressResponseBody(orginalResponse.body(), new ProgressListener() {
                                    @Override
                                    public void onProgress(long progress, long total, boolean done) {
                                    	Log.e(TAG, Looper.myLooper()+"");
                                        Log.e(TAG, "onProgress: " + "total ---->" + total + "done ---->" + progress );
                                    }
                                }))
                                .build();
                    }
                })
                .build();
        DownLoadApi api = builder.client(client)
                .build().create(DownLoadApi.class);
```
#### 4.将response写入到文件
写入的操作就简单了，代码如下
```
Call<ResponseBody> call = api.getFile("image_text.png");
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                try {
                    InputStream is = response.body().byteStream();
                    File file = new File(Environment.getExternalStorageDirectory(), "text_img.png");
                    FileOutputStream fos = new FileOutputStream(file);
                    BufferedInputStream bis = new BufferedInputStream(is);
                    byte[] buffer = new byte[1024];
                    int len;
                    while ((len = bis.read(buffer)) != -1) {
                        fos.write(buffer, 0, len);
                        fos.flush();
                    }
                    fos.close();
                    bis.close();
                    is.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                Log.e(TAG,"success");
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {

            }
        });
```
** 注意image_text.png是我事先将这张图片放入到相应路径下面的，如图，要确定能访问到才行 **
![我电脑的效果](https://github.com/Guolei1130/ATips/blob/master/image/retrofit/retrofit2.png)

#### 5.结果展示
![gif图](https://github.com/Guolei1130/ATips/blob/master/image/retrofit/retrofit_download.gif)
在下面的log日志里面可以看到当前looper为null，说明不在主线程，也可以看到进度的变化。



