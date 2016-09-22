package com.codemate.brewflop;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import java.util.Calendar;

/**
 * Created by ironman on 22/09/16.
 */

public class DayCounter {
    private static final int REQUEST_CODE = 1;

    private final Context context;
    private final AlarmManager alarmManager;

    public DayCounter(Context context) {
        this.context = context;
        this.alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
    }

    public void setAlarmIfNotExists() {
        if (!isAlarmSet()) {
            setAlarm();
        }
    }

    boolean isAlarmSet() {
        return getAlarmIntent(PendingIntent.FLAG_NO_CREATE) != null;
    }

    private PendingIntent getAlarmIntent(int flags) {
        Intent intent = new Intent(context, AlarmReceiver.class);

        return PendingIntent.getBroadcast(context, REQUEST_CODE, intent, flags);
    }

    private void setAlarm() {
        PendingIntent alarmIntent = getAlarmIntent(PendingIntent.FLAG_UPDATE_CURRENT);

        alarmManager.setRepeating(
                AlarmManager.RTC_WAKEUP,
                getTriggerTime(),
                AlarmManager.INTERVAL_DAY,
                alarmIntent
        );
    }

    long getTriggerTime() {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        calendar.set(Calendar.HOUR_OF_DAY, 6);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);

        return calendar.getTimeInMillis();
    }

    public void cancelAlarm() {
        alarmManager.cancel(getAlarmIntent(0));
    }
}
