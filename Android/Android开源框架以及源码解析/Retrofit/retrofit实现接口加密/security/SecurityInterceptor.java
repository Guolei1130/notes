package gl.com.dmeo.security;

import android.util.Log;

import java.io.IOException;
import java.util.HashMap;

import okhttp3.Interceptor;
import okhttp3.MediaType;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okio.Buffer;


/**
 * Request encryption
 */
public class SecurityInterceptor implements Interceptor {
    @Override
    public Response intercept(Chain chain) throws IOException {
        Request request = chain.request();
        RequestBody originalBody = request.body();
        Buffer buffer = new Buffer();
        originalBody.writeTo(buffer);
        StringBuffer originalBuffer = new StringBuffer();
        String string ;
        while((string = buffer.readUtf8Line()) != null){
            originalBuffer.append(string);
        }
        MediaType mediaType = MediaType.parse("text/plain; charset=utf-8");
//        String newString = encrypt(originalBuffer.toString().trim());
        String newString = "nimagecunzi";
        RequestBody body = RequestBody.create(mediaType, newString);
        request = request.newBuilder().
                header("Content-Type", body.contentType().toString()).
                header("Content-Length", String.valueOf(body.contentLength()))
                .header("nananan","lalal")
                // TODO: 2016/8/23 添加其他header
                .method(request.method(), body)
                .build();
        Log.e("MainActivity", "intercept: " + "老子兰街道你了" );
        return chain.proceed(request);
    }

    private String encrypt(String originalString){
        String[] paremasString = originalString.split("&");
        HashMap params = new HashMap();
        for (int i = 0; i < paremasString.length; i++) {
            String[] kv = paremasString[i].split("=");
            if (kv.length == 2){
                params.put(kv[0],kv[1]);
            }
        }
        // TODO: 16-8-23 按照ECN的加密流程进行加密
        return null;
    }
}
