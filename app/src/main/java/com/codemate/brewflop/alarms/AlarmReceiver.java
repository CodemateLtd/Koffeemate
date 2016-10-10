package com.codemate.brewflop.alarms;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.codemate.brewflop.DayCounter;

import java.util.Calendar;

/**
 * Created by ironman on 22/09/16.
 */
public class AlarmReceiver extends BroadcastReceiver {
    public DayCounter dayCounter;
    public Calendar calendar = Calendar.getInstance();

    @Override
    public void onReceive(Context context, Intent intent) {
        int dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK);

        if (dayOfWeek == Calendar.SATURDAY || dayOfWeek == Calendar.SUNDAY) {
            return;
        }

        if (dayCounter == null) {
            dayCounter = new DayCounter(context);
        }

        dayCounter.increment();
    }
}
