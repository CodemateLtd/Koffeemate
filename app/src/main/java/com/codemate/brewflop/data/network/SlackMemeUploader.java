package com.codemate.brewflop.data.network;

import android.util.Log;

import com.codemate.brewflop.data.repository.MemeRepository;
import com.codemate.brewflop.data.repository.RandomMemeCallback;
import com.codemate.brewflop.data.network.model.Meme;
import com.codemate.brewflop.data.network.model.SlackMessageRequest;

import java.io.IOException;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by iiro on 5.10.2016.
 */
public class SlackMemeUploader {
    private final MemeRepository memeRepository;
    private final SlackApi slackApi;

    private static SlackMemeUploader instance;
    private SlackMessageCallback callBack;

    private SlackMemeUploader(MemeRepository memeRepository, SlackApi slackApi) {
        this.memeRepository = memeRepository;
        this.slackApi = slackApi;
    }

    public static SlackMemeUploader getInstance(MemeRepository memeRepository, SlackApi slackApi) {
        if (instance == null) {
            instance = new SlackMemeUploader(memeRepository, slackApi);
        }

        return instance;
    }

    public void uploadRandomMeme(final int incidentFreeDays, final String text) {
        memeRepository.getRandomMeme(new RandomMemeCallback() {
            @Override
            public void gotRandomMeme(Meme randomMeme) {
                SlackMessageRequest messageRequest = new SlackMessageRequest(text, incidentFreeDays, randomMeme);
                postMemeToSlack(messageRequest);
            }
        });
    }

    private void postMemeToSlack(SlackMessageRequest messageRequest) {
        slackApi.sendMessage(messageRequest).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                try {
                    Log.d("response", response.body().string());
                    callBack.onMessagePostedToSlack();
                } catch (IOException e) {
                    e.printStackTrace();
                    callBack.onMessageError();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                t.printStackTrace();
                callBack.onMessageError();
            }
        });
    }

    public void setCallback(SlackMessageCallback callback) {
        this.callBack = callback;
    }
}
