package com.codemate.brewflop.alarms;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.codemate.brewflop.DayCounter;

/**
 * Created by ironman on 22/09/16.
 */
public class AlarmReceiver extends BroadcastReceiver {
    public DayCounter dayCounter;

    @Override
    public void onReceive(Context context, Intent intent) {
        if (dayCounter == null) {
            dayCounter = new DayCounter(context);
        }

        dayCounter.increment();
    }
}
