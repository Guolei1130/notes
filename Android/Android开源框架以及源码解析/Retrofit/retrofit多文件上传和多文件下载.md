### Retrofit 多文件上传与上传进度监听

#### 1.HTML FORM
html中利用form表单来上传多个文件。相关的html代码如下：
```
<html>
<body>

<form action="http://localhost/fileabout.php" enctype="multipart/form-data" method="post">
  <p>First name: <input type="file" name="file[]" id="name1" /></p>
  <p>First name: <input type="file" name="file[]" id="name2" /></p>
  <p>First name: <input type="file" name="file[]" id="name3" /></p>
  <input type="submit" value="Submit" />
</form>

</body>
</html>
```
相关的知识点
 * action form表单提交的地址
 * enctype 表示如何对表单进行编码，multipart/form-data表示有file
 * input标签中的name必须是xxx[]的格式，表示是数组中的一个元素。

#### 2. php接收代码
相关代码如下：
```
<?php	
	header('Content-Type:text/html;charset=utf-8');
	$fileArray = $_FILES['file'];//获取多个文件的信息，注意：这里的键名不包含[]

	$upload_dir = "D:\WWW"."\\"; //保存上传文件的目录
	foreach ( $fileArray['error'] as $key => $error) {
	    if ( $error == UPLOAD_ERR_OK ) { //PHP常量UPLOAD_ERR_OK=0，表示上传没有出错
	        $temp_name = $fileArray['tmp_name'][$key];
	        $file_name = $fileArray['name'][$key];
	        move_uploaded_file($temp_name, $upload_dir.$file_name);
	        echo '上传[文件'.$file_name.']成功!<br/>';
	    }else {
	        echo '上传[文件'.$key.']失败!<br/>';
	    }
	}
```
 * 所有的文件都会存在$_FILES全局变量中，多个文件的情况下，这个是一个数组[数组的形式],如下
 ```
 array(1) {
         ["file"]=> array(5) {
                   ["name"]=> array(3) { 
                            [0]=> string(5) "1.txt"
                            [1]=> string(5) "2.txt"
                            [2]=> string(5) "3.txt" }
         ["type"]=> array(3) {
                           [0]=> string(10) "text/plain"
                           [1]=> string(10) "text/plain" 
                           [2]=> string(10) "text/plain" } 
         ["tmp_name"]=> array(3) { 
                          [0]=> string(27) "C:\Windows\Temp\phpB829.tmp"
                          [1]=> string(27) "C:\Windows\Temp\phpB82A.tmp" 
                          [2]=> string(27) "C:\Windows\Temp\phpB82B.tmp" } 
        ["error"]=> array(3) {
                         [0]=> int(0)
                         [1]=> int(0) 
                         [2]=> int(0) }
        ["size"]=> array(3) {
                         [0]=> int(11)
                         [1]=> int(13)
                         [2]=> int(13) } 
 }
 ```
 * 我们需要遍历数组，并将每个文件写入到指定位置

#### 3.演示结果

#### 4.Android端实现的2中方式

##### 4.1 在api接口中指定文件名和文件数目
相关代码如下：
```
@Multipart
    @POST("/fileabout.php")
    Call<String> upload_2(@Part("filedes") String des,
                          @Part("file[]\"; filename=\"1.txt") RequestBody imgs,
                          @Part("file[]\"; filename=\"2.txt") RequestBody imgs_2,
                          @Part("file[]\"; filename=\"3.txt") RequestBody imgs_3);
```
 * 指定多个part即可
 *  ** 注意file[]， **


发送请求的代码如下：
```
File file = new File(Environment.getExternalStorageDirectory() + "/" + "1.txt");
                File file2 = new File(Environment.getExternalStorageDirectory() + "/" + "2.txt");
                File file3 = new File(Environment.getExternalStorageDirectory() + "/" + "3.txt");
                final RequestBody requestBody =
                        RequestBody.create(MediaType.parse("multipart/form-data"),file);
                final RequestBody requestBody2 =
                        RequestBody.create(MediaType.parse("multipart/form-data"),file2);
                final RequestBody requestBody3 =
                        RequestBody.create(MediaType.parse("multipart/form-data"),file3);
                Call<String> model = service.upload_2("this is txt",requestBody,requestBody2,requestBody3);
```
 * 但是上面这中和写法的灵活性太差，代码看起来也很low。

##### 4.2 可配置数目和文件名的写法。
API接口写法如下
```
@Multipart
    @POST("/fileabout.php")
    Call<String> upload_3(@Part("filedes") String des,
                          @PartMap Map<String,RequestBody> params);
```
 * 我们将多个part用map去表示，这样就可以随意配置了

相关的配置代码如下：
```
File file = new File(Environment.getExternalStorageDirectory() + "/" + "1.txt");
                File file2 = new File(Environment.getExternalStorageDirectory() + "/" + "2.txt");
                File file3 = new File(Environment.getExternalStorageDirectory() + "/" + "3.txt");
                final RequestBody requestBody =
                        RequestBody.create(MediaType.parse("multipart/form-data"),file);
                final RequestBody requestBody2 =
                        RequestBody.create(MediaType.parse("multipart/form-data"),file2);
                final RequestBody requestBody3 =
                        RequestBody.create(MediaType.parse("multipart/form-data"),file3);
//                Call<String> model = service.upload_2("this is txt",requestBody,requestBody2,requestBody3);
                Map<String,RequestBody> params = new HashMap<String, RequestBody>();
                params.put("file[]\"; filename=\""+file.getName()+"", requestBody);
                params.put("file[]\"; filename=\""+file2.getName()+"", requestBody2);
                params.put("file[]\"; filename=\""+file3.getName()+"", requestBody3);
                Call<String> model = service.upload_3("hello",params);
```
 * 当然，上面的代码也很low，但是比起第一种却要好很多，

#### 4. 结果演示

到这里多文件上传就算告一段落了，接下来弄下文件上传的进度监听。

#### 5. 如何监听上传进度！！
在前面我们知道了如何监听下载进度，但是，按照下载进度那种想法 缺没能找到解决方案。仔细一下，下载进度用拦截器，那么，上传进度是不是应该用另一个很重要的功能--** 转化器呢 **，关于这一点，我们在retrofit的demo代码里找到了答案。[链接地址](https://github.com/square/retrofit/blob/master/samples/src/main/java/com/example/retrofit/ChunkingConverter.java)
给2张图，大家自己观察。
![]()
![]()
在上面2张图中，可以观察到，转化器中出现了RequestBody，转角遇到爱。

#### 6. 改造ChunkingConverterFactory
首先，我们抛弃里面的RequestBody,我们手动往里传,也就是，去掉下面这行代码。
```
final RequestBody realBody = delegate.convert(value)
```
第二步，我们发现，在return new RequestBody()相关代码中，没有长度信息。，所以添加一下代码。
```
@Override
                    public long contentLength() throws IOException {
                        return requestBody.contentLength();
                    }
```

第三部 模仿下载的过程，写上传的过程，代码如下
```
@Override
                    public void writeTo(BufferedSink sink) throws IOException {
//                        realBody.writeTo(sink);
                        if (bufferedSink == null) {
                            //包装
                            bufferedSink = Okio.buffer(sink(sink));
                        }
                        //写入
                        requestBody.writeTo(bufferedSink);
                        //必须调用flush，否则最后一部分数据可能不会被写入
                        bufferedSink.flush();

                    }

                    private Sink sink(Sink sink) {
                        return new ForwardingSink(sink) {
                            //当前写入字节数
                            long bytesWritten = 0L;
                            //总字节长度，避免多次调用contentLength()方法
                            long contentLength = 0L;

                            @Override
                            public void write(Buffer source, long byteCount) throws IOException {
                                super.write(source, byteCount);
                                if (contentLength == 0) {
                                    //获得contentLength的值，后续不再调用
                                    contentLength = contentLength();
                                }
                                //增加当前写入的字节数
                                bytesWritten += byteCount;
                                //回调
                                listener.onProgress(bytesWritten, contentLength, bytesWritten == contentLength);
                            }
                        };
                    }
```
当然，监听器还是我们以前用那个监听器,这个类完整的代码如下
```
public class ChunkingConverterFactory extends Converter.Factory {

    @Target(PARAMETER)
    @Retention(RUNTIME)
    @interface Chunked {

    }

    private BufferedSink bufferedSink;
    private final RequestBody requestBody;

    private final ProgressListener listener;

    public ChunkingConverterFactory(RequestBody requestBody,ProgressListener listener){
        this.requestBody = requestBody;
        this.listener = listener ;
    }

    @Override
    public Converter<?, RequestBody> requestBodyConverter(Type type, Annotation[] parameterAnnotations, Annotation[] methodAnnotations, Retrofit retrofit) {

        boolean isBody = false;
        boolean isChunked = false;

        for (Annotation annotation : parameterAnnotations){
            isBody |= annotation instanceof Body;
            isChunked |= annotation instanceof Chunked;
        }

        final Converter<Object,RequestBody> delegate = retrofit
                .nextRequestBodyConverter(this,type,parameterAnnotations,methodAnnotations);

        return new Converter<Object, RequestBody>() {
            @Override
            public RequestBody convert(Object value) throws IOException {


                return new RequestBody() {
                    @Override
                    public MediaType contentType() {
                        return requestBody.contentType();
                    }


                    @Override
                    public long contentLength() throws IOException {
                        return requestBody.contentLength();
                    }

                    @Override
                    public void writeTo(BufferedSink sink) throws IOException {
//                        realBody.writeTo(sink);
                        if (bufferedSink == null) {
                            //包装
                            bufferedSink = Okio.buffer(sink(sink));
                        }
                        //写入
                        requestBody.writeTo(bufferedSink);
                        //必须调用flush，否则最后一部分数据可能不会被写入
                        bufferedSink.flush();

                    }

                    private Sink sink(Sink sink) {
                        return new ForwardingSink(sink) {
                            //当前写入字节数
                            long bytesWritten = 0L;
                            //总字节长度，避免多次调用contentLength()方法
                            long contentLength = 0L;

                            @Override
                            public void write(Buffer source, long byteCount) throws IOException {
                                super.write(source, byteCount);
                                if (contentLength == 0) {
                                    //获得contentLength的值，后续不再调用
                                    contentLength = contentLength();
                                }
                                //增加当前写入的字节数
                                bytesWritten += byteCount;
                                //回调
                                listener.onProgress(bytesWritten, contentLength, bytesWritten == contentLength);
                            }
                        };
                    }
                };
            }
        };
    }


}
```
#### 7.监听上传进度
像下载一下，我们还是通过builder去build对象，当然 也可以使用普通的方法，但是得RequestBody 写在前面，这样看起来有点怪怪的。整个代码如下
```
private void uploadProgress(){
        Retrofit.Builder builder = new Retrofit.Builder()
                .baseUrl("http://192.168.56.1");
        File file = new File(Environment.getExternalStorageDirectory() + "/" + "text_img.png");
        final RequestBody requestBody =
                RequestBody.create(MediaType.parse("multipart/form-data"),file);
        uploadfileApi api = builder.addConverterFactory(new ChunkingConverterFactory(requestBody, new ProgressListener() {
            @Override
            public void onProgress(long progress, long total, boolean done) {
                Log.e(TAG, "onProgress: 这是上传的 " + progress + "total ---->"  + total );
                Log.e(TAG, "onProgress: " + Looper.myLooper());
            }
        })).addConverterFactory(GsonConverterFactory.create()).build().create(uploadfileApi.class);
        Call<String> model = api.upload("hh",requestBody);
        model.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {

            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {

            }
        });
    }
```

#### 8.测试 演示

#### 9 多文件上传 优化方法
```
RequestBody requestBody = new MultipartBody.Builder()
				.setType(MediaType.parse("multipart/form-data"))
				.addFormDataPart("name", "aa")
				.addFormDataPart("action", "upload")
				.addFormDataPart("image", file_1.getName(),
						requestBody_1)
				.addFormDataPart("image",file_2.getName(),requestBody_2)
				.build();
```

```
    @POST("/retrofit.php")
    Call<String> upload_2(
            @Body RequestBody imgs
    );
```


