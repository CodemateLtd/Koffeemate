package com.codemate.brewflop.ui;

import com.codemate.brewflop.DayCountUpdater;
import com.codemate.brewflop.data.network.SlackMemeUploader;

/**
 * Created by iiro on 6.10.2016.
 */
public class MainPresenter {
    private final MainView mainView;
    private final DayCountUpdater dayCountUpdater;
    private final SlackMemeUploader memeUploader;

    public MainPresenter(MainView mainView, DayCountUpdater dayCountUpdater, SlackMemeUploader memeUploader) {
        this.mainView = mainView;
        this.dayCountUpdater = dayCountUpdater;
        this.memeUploader = memeUploader;
    }

    public void resetCounterAndInformAboutANoob(String message) {
        dayCountUpdater.reset();

        memeUploader.uploadRandomMeme(message);
    }
}
