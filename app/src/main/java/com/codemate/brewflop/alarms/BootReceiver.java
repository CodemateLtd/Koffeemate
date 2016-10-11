package com.codemate.brewflop.alarms;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/**
 * Created by iiro on 22.9.2016.
 */
public class BootReceiver extends BroadcastReceiver {
    public DayUpdateScheduler dayUpdateScheduler;

    @Override
    public void onReceive(Context context, Intent intent) {
        // For testing
        if (dayUpdateScheduler == null) {
            dayUpdateScheduler = new DayUpdateScheduler(context);
        }

        dayUpdateScheduler.setAlarm();
    }
}
