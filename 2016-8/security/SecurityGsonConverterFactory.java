package gl.com.dmeo.security;

import com.google.gson.Gson;
import com.google.gson.TypeAdapter;
import com.google.gson.reflect.TypeToken;

import java.lang.annotation.Annotation;
import java.lang.reflect.Type;

import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Converter;
import retrofit2.Retrofit;

public class SecurityGsonConverterFactory extends Converter.Factory{

    public  static SecurityGsonConverterFactory create(){
        return create(new Gson());
    }

    public static SecurityGsonConverterFactory create(Gson gson){
        return new SecurityGsonConverterFactory(gson);
    }

    private final Gson gson;

    public SecurityGsonConverterFactory(Gson gson){
        if (gson == null) throw new NullPointerException("gson == null");
        this.gson = gson;
    }

    @Override
    public Converter<ResponseBody, ?> responseBodyConverter(Type type, Annotation[] annotations, Retrofit retrofit) {

        TypeAdapter<?> adapter = gson.getAdapter(TypeToken.get(type));
        return new SecurityGsonResponseBodyCoverter<>(gson,adapter);
    }

    @Override
    public Converter<?, RequestBody> requestBodyConverter(Type type,Annotation[] parameterAnnotations, Annotation[] methodAnnotations, Retrofit retrofit) {
        TypeAdapter<?> adapter = gson.getAdapter(TypeToken.get(type));
        return new SecurityGsonRequestBodyCoverter<>(gson,adapter);
    }
}
