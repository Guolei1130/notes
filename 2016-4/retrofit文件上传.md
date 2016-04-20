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

