package com.codemate.brewflop;

import android.app.Application;

import com.codemate.brewflop.alarms.DayUpdateScheduler;

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
        DayUpdateScheduler dayUpdateScheduler = new DayUpdateScheduler(this);
        dayUpdateScheduler.setAlarm();
    }
}
