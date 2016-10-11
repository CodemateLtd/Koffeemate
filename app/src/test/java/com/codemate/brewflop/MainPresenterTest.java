package com.codemate.brewflop;

import com.codemate.brewflop.data.network.SlackMemeUploader;
import com.codemate.brewflop.ui.MainPresenter;
import com.codemate.brewflop.ui.MainView;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Created by iiro on 6.10.2016.
 */
public class MainPresenterTest {
    @Mock
    private MainView mainView;

    @Mock
    private DayCountUpdater dayCountUpdater;

    @Mock
    private SlackMemeUploader slackMemeUploader;

    private MainPresenter mainPresenter;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);

        mainPresenter = new MainPresenter(mainView, dayCountUpdater, slackMemeUploader);
    }

    @Test
    public void shouldUploadMemeWithCorrectDayCountWhenCounterReseted() {
        when(dayCountUpdater.getDayCount()).thenReturn(1337);
        mainPresenter.resetCounterAndInformAboutANoob("Test noob");

        verify(dayCountUpdater).reset();
        verify(slackMemeUploader).uploadRandomMeme(1337, "Test noob");
    }
}
