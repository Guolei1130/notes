package gl.com.dmeo;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;

/**
 * Created by guolei on 16-8-24.
 * ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
 * |        没有神兽，风骚依旧！          |
 * |        QQ:1120832563             |
 * ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
 */


public interface GithubService {
    @FormUrlEncoded
    @POST("/retrofit.php")
    Call<User> getUserinfo(@Field("username") String username,
                           @Field("password") String password);

    @GET("/users/{user}")
    Call<User> getFeed(@Path("user") String user);
}
