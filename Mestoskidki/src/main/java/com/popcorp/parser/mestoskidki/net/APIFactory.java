package com.popcorp.parser.mestoskidki.net;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import rx.Scheduler;
import rx.schedulers.Schedulers;

import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class APIFactory {

    public static final String BASE_URL = "http://mestoskidki.ru/";

    private static API api;
    private static Scheduler scheduler;

    public static API getAPI(){
        if (api == null) {
            OkHttpClient.Builder okHttpClientBuilder = new OkHttpClient.Builder();
            okHttpClientBuilder.connectTimeout(60, TimeUnit.SECONDS);
            okHttpClientBuilder.readTimeout(60, TimeUnit.SECONDS);


            Retrofit retrofit = new Retrofit.Builder()
                    .client(okHttpClientBuilder.build())
                    .baseUrl(BASE_URL)
                    .addCallAdapterFactory(RxJavaCallAdapterFactory.create()).build();

            api = retrofit.create(API.class);
        }
        return api;
    }

    public static Scheduler getScheduler(){
        if (scheduler == null){
            scheduler = Schedulers.from(Executors.newFixedThreadPool(10));
        }
        return scheduler;
    }
}
