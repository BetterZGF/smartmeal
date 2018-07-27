package com.aaron.group.smartmeal.retrofit;

import java.io.IOException;

import okhttp3.HttpUrl;
import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

/**
 * 说明:

 */

public class CommonsInterceptor implements Interceptor {
    @Override
    public Response intercept(Chain chain) throws IOException {
        Request original = chain.request();
        //添加公共参数
        HttpUrl originalHttpUrl = original.url();
        HttpUrl url = originalHttpUrl.newBuilder()
                .addQueryParameter("key", "f53a25d4c632")
                .build();
        // Request customization: add request headers
        Request.Builder requestBuilder = original.newBuilder()
                .addHeader("Connection", "Keep-Alive")
                .addHeader("Content-Type", "application/json")
                .url(url);

        Request request = requestBuilder.build();
        return chain.proceed(request);
    }
}
