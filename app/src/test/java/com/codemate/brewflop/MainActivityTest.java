package com.codemate.brewflop;

import com.codemate.brewflop.network.SlackMemeUploader;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Created by iiro on 6.10.2016.
 */
public class MainActivityTest {
    @Mock
    private MainView mainView;

    @Mock
    private DayCounter dayCounter;

    @Mock
    private SlackMemeUploader slackMemeUploader;

    private MainPresenter mainPresenter;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);

        mainPresenter = new MainPresenter(mainView, dayCounter, slackMemeUploader);
    }

    @Test
    public void shouldShowPromptWhenAskingForGuiltyCoffeeNoob() {
        mainPresenter.askForGuiltyCoffeeNoob();

        verify(mainView).showGuiltyCoffeeNoobPrompt();
    }

    @Test
    public void shouldUploadMemeWithCorrectDayCountWhenCounterReseted() {
        when(dayCounter.getDayCount()).thenReturn(1337);
        mainPresenter.resetCounterAndInformAboutANoob("Test noob");

        verify(dayCounter).reset();
        verify(slackMemeUploader).uploadRandomMeme(1337, "Test noob");
    }
}
