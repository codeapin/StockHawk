package com.udacity.stockhawk.data;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.udacity.stockhawk.BuildConfig;

import java.text.DateFormat;

import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

public class StockApiClient {
    private static final String BASE_URL = "https://www.quandl.com/api/v3/datasets/WIKI/";
    private static StockApi sInstance;

    public static StockApi getStockApiClient(){
        if(sInstance == null){
            sInstance = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .client(getOkHttpClient())
                    .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                    .addConverterFactory(GsonConverterFactory.create(getJson()))
                    .build()
                    .create(StockApi.class);
        }
        return sInstance;
    }

    private static Gson getJson(){
        return new GsonBuilder().setDateFormat(DateFormat.LONG).create();
    }

    private static OkHttpClient getOkHttpClient(){
        return new OkHttpClient.Builder()
                .addInterceptor(new HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY))
                .addInterceptor(chain -> {
                    Request original = chain.request();
                    HttpUrl url = original.url().newBuilder()
                            .addQueryParameter("api_key", BuildConfig.QUANDL_API_KEY).build();
                    Request request = original.newBuilder()
                            .url(url)
                            .build();
                    return chain.proceed(request);
                })
                .build();
    }
}
