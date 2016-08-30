package com.gl.retrofitdemo;

import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.gl.retrofitdemo.download.ProgressResponseBody;
import com.gl.retrofitdemo.download.ProgressResponseListener;
import com.gl.retrofitdemo.upload.ProgressListener;
import com.gl.retrofitdemo.upload.ProgressRequestBody;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import okhttp3.Cache;
import okhttp3.CacheControl;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.Interceptor;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";
    public static final MediaType MEDIA_TYPE_TEXT
            = MediaType.parse("*/*");

    Button get,post,downloadfile,uploadfile;
    TextView content;

    OkHttpClient client;
    OkHttpClient client_progress;

    private static final String PNG_PATH = Environment.getExternalStorageDirectory().getPath();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        init();
        initView();
        bindListener();
        bindData();
        Log.e(TAG, "onCreate: " + Environment.getExternalStorageDirectory().getAbsolutePath());
        File file = new File(PNG_PATH,"m.png");
        if (file.exists()){
            file.delete();
        }
    }

    private void init() {
        if (null == client){
            client = new OkHttpClient();
        }
    }

    private void initView() {
        get = (Button) findViewById(R.id.get);
        post = (Button) findViewById(R.id.post);
        downloadfile = (Button) findViewById(R.id.download_file);
        uploadfile = (Button) findViewById(R.id.uploadfile);


        content = (TextView) findViewById(R.id.content);
    }

    private void bindListener() {
        /**
         * get请求
         */
        get.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Request request = new Request.Builder()
                        .url("http://www.baidu.com/s?word=Error%3ACause%3A%20peer%20not%20authenticated&tn=39015028_hao_pg&ie=utf-8")
                        .build();
                client.newCall(request)
                        .enqueue(new Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {

                    }

                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
                        Log.e(TAG, "onResponse: " + response.body().string() );
                    }
                });
            }
        });

        /**
         * post请求（表单）
         */
        post.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                RequestBody body = new FormBody.Builder()
                        .add("username","guolei")
                        .add("password","123456")
                        .build();
                Request postRequest = new Request.Builder()
                        // 注意这里的ip要没翻墙的那个ip
                        .url("http://192.168.0.107/retrofit.php")
                        .post(body)
                        .build();

                client.newCall(postRequest).enqueue(new Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {
                        Log.e(TAG, "onFailure: " + "error" + e );
                    }

                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
                        Log.e(TAG, "onResponse: " + response.body().string() );
                    }
                });
            }
        });

        /**
         * 下载文件及进度监听
         */
        downloadfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (client_progress == null){
                    client_progress = new OkHttpClient.Builder()
                            .addNetworkInterceptor(new Interceptor() {
                                @Override
                                public Response intercept(Chain chain) throws IOException {
                                    Response originalResponse = chain.proceed(chain.request());
                                    return  originalResponse.newBuilder().body(new ProgressResponseBody(originalResponse.body(), new ProgressResponseListener() {
                                        @Override
                                        public void onResponseProgress(long bytesRead, long contentength, boolean done) {
                                            Log.e(TAG, "onResponseProgress: " + "总长度---->"+contentength + ">:已下载进度----->"+bytesRead);
                                        }
                                    })).build();
                                }
                            })
                            .build();
                }
                Request download_file = new Request.Builder()
                        .url("http://192.168.0.107/files/image_text.png")
                        .build();
                client_progress.newCall(download_file).enqueue(new Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {
                        Log.e(TAG, "onFailure: " + e.getMessage() );
                    }
                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
                        try {
                            InputStream is = response.body().byteStream();
                            File file = new File(Environment.getExternalStorageDirectory(),"m.png");

                            FileOutputStream fos = new FileOutputStream(file);
                            BufferedInputStream bis = new BufferedInputStream(is);
                            byte[] buffer = new byte[1024];
                            int len;
                            while ((len = bis.read(buffer))!=-1){
                                fos.write(buffer,0,len);
                                fos.flush();
                            }
                            fos.close();
                            bis.close();
                            is.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                        Log.e(TAG, "onResponse: " + "success" );
                    }
                });
            }
        });

        /**
         * 上传文件
         */
        uploadfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                File file = new File(Environment.getExternalStorageDirectory(),"uploadfile.txt");
                final RequestBody requestBody = new MultipartBody.Builder()
                        .setType(MultipartBody.FORM)
                        .addFormDataPart("file", "uploadfile.txt",
                                RequestBody.create(MEDIA_TYPE_TEXT, file))
                        .build();
                Request request = new Request.Builder()
                        .url("http://192.168.0.107/upload_one_file.php")
                        .post(new ProgressRequestBody(requestBody, new ProgressListener() {
                            @Override
                            public void progress(long bytesRead, long contentLength, boolean done) {
                                Log.e(TAG, "progress: " + "文件大小---->" + contentLength + ":-D 已上传进度-->" +bytesRead);
                            }
                        }))
                        .build();
                client.newCall(request).enqueue(new Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {
                        Log.e(TAG, "onFailure: " + e.getMessage() );
                    }

                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
                        if (response.isSuccessful()) {
                            Log.e(TAG, "onResponse: " + response );
                        }
                    }
                });
            }
        });
    }

    /**
     * post json 数据
     */
    private void postJson(){

    }

    private void cacheAb(){
        CacheControl cacheControl = new CacheControl.Builder()

                .build();

        Cache cache = new Cache(new File(""),10 * 1024 *1024);
        
    }

    private void bindData() {

    }
}
