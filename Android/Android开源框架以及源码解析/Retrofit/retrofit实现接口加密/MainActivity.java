package gl.com.dmeo;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import java.io.IOException;

import gl.com.dmeo.security.SecurityGsonConverterFactory;
import gl.com.dmeo.security.SecurityInterceptor;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import retrofit2.Retrofit;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = MainActivity.class.getSimpleName();
    SharedPreferences mShared;
    SharedPreferences.Editor editor;
    OkHttpClient client ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        findViewById(R.id.buttonPanel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                net();
            }
        });

    }

    private void net() {
        Retrofit.Builder builder = new Retrofit.Builder()
                .baseUrl("http://125.33.118.109")
                .addConverterFactory(SecurityGsonConverterFactory.create());

        OkHttpClient client = new OkHttpClient().newBuilder()
                .addInterceptor(new SecurityInterceptor())
                .build();

        Retrofit retrofit = builder.client(client).build();

        GithubService service = retrofit.create(GithubService.class);
        service.getUserinfo("Guolei1130","123456").enqueue(new retrofit2.Callback<User>() {
            @Override
            public void onResponse(retrofit2.Call<User> call, retrofit2.Response<User> response) {
                Log.e(TAG, "onResponse: " + response.body().name);
            }

            @Override
            public void onFailure(retrofit2.Call<User> call, Throwable t) {
                Log.e(TAG, "onFailure: " + t.getMessage() );
            }
        });
//
//        service.getFeed("Guolei1130").enqueue(new retrofit2.Callback<User>() {
//            @Override
//            public void onResponse(retrofit2.Call<User> call, retrofit2.Response<User> response) {
//                Log.e(TAG, "onResponse: " + response.body().name );
//            }
//
//            @Override
//            public void onFailure(retrofit2.Call<User> call, Throwable t) {
//                Log.e(TAG, "onFailure: " + t.getMessage() );
//            }
//        });
    }


}
