package com.codemate.brewflop;

import android.app.Application;

import com.codemate.brewflop.alarms.DayUpdater;

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
        DayUpdater dayUpdater = new DayUpdater(this);
        dayUpdater.setAlarm();
    }
}
