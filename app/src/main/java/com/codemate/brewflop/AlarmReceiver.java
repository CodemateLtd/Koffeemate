package com.codemate.brewflop;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;

/**
 * Created by ironman on 22/09/16.
 */
public class AlarmReceiver extends BroadcastReceiver {
    DayCounter dayCounter;

    @Override
    public void onReceive(Context context, Intent intent) {
        if (dayCounter == null) {
            dayCounter = new DayCounter(context, LocalBroadcastManager.getInstance(context));
        }

        dayCounter.increment();
    }
}
