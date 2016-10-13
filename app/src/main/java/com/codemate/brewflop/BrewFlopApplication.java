package com.codemate.brewflop;

import android.app.Application;

import com.codemate.brewflop.alarms.DayUpdateScheduler;
import com.codemate.brewflop.injection.DaggerNetComponent;
import com.codemate.brewflop.injection.NetComponent;
import com.codemate.brewflop.injection.NetModule;

/**
 * Created by iiro on 22.9.2016.
 */
public class BrewFlopApplication extends Application {
    private static BrewFlopApplication instance;
    private NetComponent netComponent;

    @Override
    public void onCreate() {
        super.onCreate();

        instance = this;

        netComponent = DaggerNetComponent.builder()
                .netModule(new NetModule("https://hooks.slack.com/services/"))
                .build();

        setDayCounterAlarm();
    }

    public static NetComponent netComponent() {
        return instance.netComponent;
    }

    private void setDayCounterAlarm() {
        DayUpdateScheduler dayUpdateScheduler = new DayUpdateScheduler(this);
        dayUpdateScheduler.setAlarm();
    }
}
