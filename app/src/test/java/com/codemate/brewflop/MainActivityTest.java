package com.codemate.brewflop;

import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.speech.RecognizerIntent;

import com.codemate.brewflop.network.SlackMemeUploader;
import com.codemate.brewflop.ui.MainActivity;
import com.codemate.brewflop.ui.MainPresenter;
import com.codemate.brewflop.ui.MainView;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import java.util.ArrayList;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.robolectric.Shadows.shadowOf;

/**
 * Created by iiro on 6.10.2016.
 */
@RunWith(RobolectricTestRunner.class)
@Config(constants = BuildConfig.class, sdk = Build.VERSION_CODES.LOLLIPOP)
public class MainActivityTest {
    @Mock
    private MainView mainView;

    @Mock
    private DayCounter dayCounter;

    @Mock
    private SlackMemeUploader slackMemeUploader;

    private MainActivity mainActivity;
    private MainPresenter mainPresenter;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);

        mainActivity = Robolectric.setupActivity(MainActivity.class);

        mainPresenter = new MainPresenter(mainView, dayCounter, slackMemeUploader);
    }
    @Test
    public void shouldShowConfirmDialogWithCorrectNameWhenAskedForGuiltyCoffeeNoob() {
        mainActivity.askForGuiltyCoffeeNoob();

        shadowOf(mainActivity).receiveResult(
                new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH),
                Activity.RESULT_OK,
                new Intent().putExtra(RecognizerIntent.EXTRA_RESULTS, new ArrayList<>())
        );

        verify(mainView).confirmGuiltyCoffeeNoob("JORMA");
    }

    @Test
    public void shouldUploadMemeWithCorrectDayCountWhenCounterReseted() {
        when(dayCounter.getDayCount()).thenReturn(1337);
        mainPresenter.resetCounterAndInformAboutANoob("Test noob");

        verify(dayCounter).reset();
        verify(slackMemeUploader).uploadRandomMeme(1337, "Test noob");
    }
}
