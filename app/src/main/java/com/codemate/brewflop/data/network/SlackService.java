package com.codemate.brewflop.data.network;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by iiro on 4.10.2016.
 */
public class SlackService {
    private SlackApi slackApi;

    public SlackService() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://hooks.slack.com/services/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        slackApi = retrofit.create(SlackApi.class);
    }

    public SlackApi getApi() {
        return slackApi;
    }

}
