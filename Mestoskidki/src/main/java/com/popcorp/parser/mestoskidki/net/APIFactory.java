package com.popcorp.parser.mestoskidki.net;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;

import java.util.concurrent.TimeUnit;

public class APIFactory {

    private static API api;

    public static API getAPI(){
        if (api == null) {
            OkHttpClient.Builder okHttpClientBuilder = new OkHttpClient.Builder();
            okHttpClientBuilder.connectTimeout(60, TimeUnit.SECONDS);
            okHttpClientBuilder.readTimeout(60, TimeUnit.SECONDS);

            Retrofit retrofit = new Retrofit.Builder()
                    .client(okHttpClientBuilder.build())
                    .baseUrl("http://mestoskidki.ru/")
                    .addCallAdapterFactory(RxJavaCallAdapterFactory.create()).build();

            api = retrofit.create(API.class);
        }
        return api;
    }
}
