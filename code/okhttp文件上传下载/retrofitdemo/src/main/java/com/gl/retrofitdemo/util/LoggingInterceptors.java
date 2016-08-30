package com.gl.retrofitdemo.util;


import java.io.IOException;
import java.util.logging.Logger;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

public class LoggingInterceptors implements Interceptor {

    private static final Logger logger = Logger.getLogger(LoggingInterceptors.class.getName());

    @Override
    public Response intercept(Chain chain) throws IOException {

        long t1 = System.nanoTime();
        Request  request = chain.request();
        logger.info(String.format("Sending request %s on %s%n%s",request.url(),chain.connection(),request.headers()));

        Response response = chain.proceed(request);

        long t2 = System.nanoTime();
        logger.info(String.format("Received response for %s in %.1fms%n%s", request.url(), (t2 - t1) / 1e6d, response.headers()));
        return null;
    }

}
