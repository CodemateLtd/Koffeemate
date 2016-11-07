package com.codemate.brewflop.data.repository;

import android.util.Log;

import com.codemate.brewflop.data.network.model.Meme;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Created by ironman on 11/10/16.
 */
public class FirebaseMemeRepository implements MemeRepository {
    private final DatabaseReference memeReference;
    private final Random random;

    public FirebaseMemeRepository() {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        //database.setPersistenceEnabled(true);

        memeReference = database.getReference().child("memes");
        memeReference.keepSynced(true);

        random = new Random();
    }

    @Override
    public void getRandomMeme(final RandomMemeCallback callback) {
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
}
