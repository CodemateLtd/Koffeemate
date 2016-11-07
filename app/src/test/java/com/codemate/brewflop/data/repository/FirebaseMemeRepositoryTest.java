package com.codemate.brewflop.data.repository;

import com.codemate.brewflop.data.network.model.Meme;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.junit.Before;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;

import static org.hamcrest.Matchers.not;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class FirebaseMemeRepositoryTest {
    private FirebaseDatabase firebaseDatabase;
    private FirebaseMemeRepository memeRepository;
    private DatabaseReference mockMemeReference;

    @Before
    public void setUp() {
        firebaseDatabase = mock(FirebaseDatabase.class);

        DatabaseReference outerMockReference = mock(DatabaseReference.class);
        when(firebaseDatabase.getReference()).thenReturn(outerMockReference);

        mockMemeReference = mock(DatabaseReference.class);
        when(outerMockReference.child("memes")).thenReturn(mockMemeReference);

        memeRepository = new FirebaseMemeRepository(firebaseDatabase);
    }

    @Test
    public void shouldAddListenerWhenGettingRandomMeme() {
        memeRepository.getRandomMeme(any(RandomMemeCallback.class));

        verify(mockMemeReference).addListenerForSingleValueEvent(any(ValueEventListener.class));
    }

    // TODO: Test the getRandomMeme method more thoroughly!

    @Test
    public void getMemeAtPositionReturnsCorrectMeme() {
        DataSnapshot first = mock(DataSnapshot.class);
        DataSnapshot second = mock(DataSnapshot.class);
        DataSnapshot third = mock(DataSnapshot.class);
        List<DataSnapshot> snapshots = Arrays.asList(first, second, third);

        Meme firstMeme = new Meme();
        Meme secondMeme = new Meme();
        Meme thirdMeme = new Meme();

        when(first.getValue(Meme.class)).thenReturn(firstMeme);
        when(second.getValue(Meme.class)).thenReturn(secondMeme);
        when(third.getValue(Meme.class)).thenReturn(thirdMeme);

        assertThat(memeRepository.getMemeAtPostion(0, snapshots), is(firstMeme));
        assertThat(memeRepository.getMemeAtPostion(1, snapshots), is(secondMeme));
        assertThat(memeRepository.getMemeAtPostion(2, snapshots), is(thirdMeme));
    }
}
