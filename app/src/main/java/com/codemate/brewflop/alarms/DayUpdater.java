package com.codemate.brewflop.alarms;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

import com.codemate.brewflop.Constants;

import java.util.Calendar;

/**
 * Created by ironman on 22/09/16.
 */

public class DayUpdater {
    private static final int REQUEST_CODE = 1;

    private final Context context;
    private final AlarmManager alarmManager;

    public DayUpdater(Context context) {
        this.context = context;
        this.alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
    }

    public void setAlarm() {
        PendingIntent alarmIntent = getAlarmIntent(PendingIntent.FLAG_UPDATE_CURRENT);
        long interval = Constants.DAY_UPDATE_INTERVAL;
        long firstTriggerTime = getTriggerTime() + interval;

        alarmManager.setRepeating(
                AlarmManager.RTC_WAKEUP,
                firstTriggerTime,
                interval,
                alarmIntent
        );
    }

    private PendingIntent getAlarmIntent(int flags) {
        Intent intent = new Intent(context, AlarmReceiver.class);

        return PendingIntent.getBroadcast(context, REQUEST_CODE, intent, flags);
    }

    public long getTriggerTime() {
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
