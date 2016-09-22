package com.codemate.brewflop;

import android.app.Application;

/**
 * Created by iiro on 22.9.2016.
 */
public class BrewFlopApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();

        setDayCounterAlarm();
    }

    private void setDayCounterAlarm() {
        DayCounter dayCounter = new DayCounter(this);
        dayCounter.setAlarmIfNotExists();
    }
}
