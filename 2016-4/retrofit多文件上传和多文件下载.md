### Retrofit 多文件上传

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

#### 5. 总结
我这里的多文件上传 没有考虑文件大小的问题，但是，效果好歹出来了不是么，也不知道能不能把上传进度监听到，到现在还没有想法，希望大神们给点思路

