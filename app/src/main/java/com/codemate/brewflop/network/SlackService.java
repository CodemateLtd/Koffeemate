package com.codemate.brewflop.network;

import com.codemate.brewflop.BuildConfig;
import com.codemate.brewflop.network.model.SlackMessageRequest;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.Body;
import retrofit2.http.POST;

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

    public interface SlackApi {
        @POST(BuildConfig.SLACK_CHANNEL_PATH)
        Call<ResponseBody> sendMessage(@Body SlackMessageRequest slackMessageRequest);
    }
}
