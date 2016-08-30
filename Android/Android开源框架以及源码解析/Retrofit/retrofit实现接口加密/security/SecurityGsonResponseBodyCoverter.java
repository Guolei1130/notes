package gl.com.dmeo.security;

import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.StringReader;

import okhttp3.ResponseBody;
import retrofit2.Converter;

import static android.content.ContentValues.TAG;

public class SecurityGsonResponseBodyCoverter<T> implements Converter<ResponseBody,T> {

    private final Gson gson;
    private final TypeAdapter<T> adapter;

    SecurityGsonResponseBodyCoverter(Gson gson,TypeAdapter<T> adapter){
        this.gson = gson;
        this.adapter = adapter;
    }

    @Override
    public T convert(ResponseBody value) throws IOException {
        String encryptString = value.string();
        JSONObject jsonObject = null;
        try {
             jsonObject = new JSONObject(encryptString.trim());
             jsonObject.put("name","xiugai");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        Log.e("MainActivity", "convert: " + "老子朱啊哈uni了");
        JsonReader jsonReader = gson.newJsonReader(new StringReader(jsonObject.toString()));
        try {
            return adapter.read(jsonReader);

        }finally {
            value.close();
        }
    }
}
