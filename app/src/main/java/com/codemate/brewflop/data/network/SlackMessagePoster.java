package com.codemate.brewflop.data.network;

import com.codemate.brewflop.data.network.model.Attachment;
import com.codemate.brewflop.data.network.model.SlackMessageRequest;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by iiro on 5.10.2016.
 */
public class SlackMessagePoster {
    private final SlackWebHookApi slackWebHookApi;

    public SlackMessagePoster(SlackWebHookApi slackWebHookApi) {
        this.slackWebHookApi = slackWebHookApi;
    }

    public void uploadRandomMeme(final String text, final SlackMessageCallback callback) {
        Attachment attachment = new Attachment.Builder()
                .fallback("PLACEHOLDER")
                .color("#6F4E37")
                .imageUrl("https://www.google.fi/images/branding/googlelogo/2x/googlelogo_color_272x92dp.png")
                .build();

        SlackMessageRequest messageRequest = new SlackMessageRequest(text, attachment);
        postMemeToSlack(messageRequest, callback);
    }

    private void postMemeToSlack(SlackMessageRequest messageRequest, final SlackMessageCallback callback) {
        slackWebHookApi.sendMessage(messageRequest).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    callback.onMessagePostedToSlack();
                } else {
                    callback.onMessageError();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                t.printStackTrace();
                callback.onMessageError();
            }
        });
    }
}