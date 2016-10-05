package com.codemate.brewflop.network;

import android.util.Log;

import com.codemate.brewflop.network.model.Meme;
import com.codemate.brewflop.network.model.SlackMessageRequest;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by iiro on 5.10.2016.
 */
public class SlackMemeUploader {
    private final Random random;
    private final DatabaseReference memeReference;
    private final SlackService.SlackApi slackApi;

    private static SlackMemeUploader instance;

    private SlackMemeUploader() {
        random = new Random();

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        database.setPersistenceEnabled(true);

        memeReference = database.getReference().child("memes");
        memeReference.keepSynced(true);

        slackApi = new SlackService().getApi();
    }

    public static SlackMemeUploader getInstance() {
        if (instance == null) {
            instance = new SlackMemeUploader();
        }

        return instance;
    }

    public void uploadRandomMeme(final int incidentFreeDays, final String text) {
        getRandomMeme(new RandomMemeCallback() {
            @Override
            public void gotRandomMeme(Meme randomMeme) {
                SlackMessageRequest messageRequest = new SlackMessageRequest(text, incidentFreeDays, randomMeme);
                postMemeToSlack(messageRequest);
            }
        });
    }

    private void getRandomMeme(final RandomMemeCallback callback) {
        memeReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.hasChildren()) {
                    int randomIndex = random.nextInt((int) dataSnapshot.getChildrenCount());

                    Meme randomMeme = getChildAtIndex(randomIndex, dataSnapshot.getChildren());
                    callback.gotRandomMeme(randomMeme);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e("SlackMemeUploader", databaseError.toString());
            }
        });
    }

    private static Meme getChildAtIndex(int index, Iterable<DataSnapshot> iterable) {
        List<Meme> memeList = new ArrayList<>();

        for (DataSnapshot snapshot : iterable) {
            memeList.add(snapshot.getValue(Meme.class));
        }

        return memeList.get(index);
    }

    private void postMemeToSlack(SlackMessageRequest messageRequest) {
        slackApi.sendMessage(messageRequest).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                try {
                    Log.d("response", response.body().string());
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                t.printStackTrace();
            }
        });
    }

    private interface RandomMemeCallback {
        void gotRandomMeme(Meme randomMeme);
    }
}
