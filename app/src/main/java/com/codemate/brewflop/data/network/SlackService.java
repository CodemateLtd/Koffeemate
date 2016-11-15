package com.codemate.brewflop.data.network;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by iiro on 4.10.2016.
 */
public class SlackService {
    private SlackService() { }

    public static SlackWebHookApi getWebhookApi(String baseUrl) {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(baseUrl)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        return retrofit.create(SlackWebHookApi.class);
    }

    public static SlackApi getApi(String baseUrl) {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(baseUrl)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        return retrofit.create(SlackApi.class);
    }
}
