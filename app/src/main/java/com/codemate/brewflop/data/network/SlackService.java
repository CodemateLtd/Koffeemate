package com.codemate.brewflop.data.network;

import okhttp3.Dispatcher;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by iiro on 4.10.2016.
 */
public class SlackService {
    public static SlackApi getApi(HttpUrl baseUrl) {
        return getApi(new Dispatcher(), baseUrl);
    }

    public static SlackApi getApi(Dispatcher dispatcher, HttpUrl baseUrl) {
        OkHttpClient client = new OkHttpClient.Builder()
                .dispatcher(dispatcher)
                .build();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(baseUrl)
                .client(client)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        return retrofit.create(SlackApi.class);
    }
}
