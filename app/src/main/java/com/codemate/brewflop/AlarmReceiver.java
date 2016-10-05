package com.codemate.brewflop;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/**
 * Created by ironman on 22/09/16.
 */
public class AlarmReceiver extends BroadcastReceiver {
    DayCounter dayCounter;

    @Override
    public void onReceive(Context context, Intent intent) {
        if (dayCounter == null) {
            dayCounter = new DayCounter(context);
        }

        dayCounter.increment();
    }
}
