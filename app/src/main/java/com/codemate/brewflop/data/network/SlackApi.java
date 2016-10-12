package com.codemate.brewflop.data.network;

import com.codemate.brewflop.BuildConfig;
import com.codemate.brewflop.data.network.model.SlackMessageRequest;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

/**
 * Created by iiro on 12.10.2016.
 */
public interface SlackApi {
    @POST(BuildConfig.SLACK_CHANNEL_PATH)
    Call<ResponseBody> sendMessage(@Body SlackMessageRequest slackMessageRequest);
}
