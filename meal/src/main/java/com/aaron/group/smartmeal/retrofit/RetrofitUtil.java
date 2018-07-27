package com.aaron.group.smartmeal.retrofit;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;

import java.io.File;
import java.util.concurrent.TimeUnit;

import okhttp3.Cache;
import okhttp3.CertificatePinner;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * 说明:

 */

public class RetrofitUtil {
    private static Retrofit retrofit = null;

    public static void init(Context context)
    {
        //初始化OkHttpClient
        HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor(
                new HttpLoggingInterceptor.Logger() {
                    @Override
                    public void log(String message) {
                        if (!TextUtils.isEmpty(message))
                        {
                            Log.e("retrofit2.0--------", "收到响应: " + message);
                        }
                    }
                }
        );
        interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        OkHttpClient client = new OkHttpClient.Builder()
                .addInterceptor(interceptor)
                .addInterceptor(new CommonsInterceptor())
                .retryOnConnectionFailure(false)
                .cache(new Cache(new File(context.getExternalCacheDir().getAbsolutePath()), 10*3600*24))
                .connectTimeout(20, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .certificatePinner(new CertificatePinner.Builder().build())
                .build();
        retrofit = new Retrofit.Builder()
                //设置OKHttpClient
                .client(client)
                //设置baseUrl,注意，baseUrl必须后缀"/"
                .baseUrl("https://apicloud.mob.com/")
                //添加Gson转换器
                .addConverterFactory(GsonConverterFactory.create())
                .build();
    }

    public static Retrofit obtainRetrofit()
    {
        if(null!=retrofit)
        {
            return retrofit;
        }
        else
        {
            return null;
        }
    }
}
