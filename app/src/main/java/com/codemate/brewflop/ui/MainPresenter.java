package com.codemate.brewflop.ui;

import com.codemate.brewflop.DayCounter;
import com.codemate.brewflop.network.SlackMemeUploader;

/**
 * Created by iiro on 6.10.2016.
 */
public class MainPresenter {
    private final MainView mainView;
    private final DayCounter dayCounter;
    private final SlackMemeUploader memeUploader;

    public MainPresenter(MainView mainView, DayCounter dayCounter, SlackMemeUploader memeUploader) {
        this.mainView = mainView;
        this.dayCounter = dayCounter;
        this.memeUploader = memeUploader;
    }

    public void resetCounterAndInformAboutANoob(String name) {
        int incidentFreeDays = dayCounter.getDayCount();
        dayCounter.reset();

        memeUploader.uploadRandomMeme(incidentFreeDays, name);
    }
}
