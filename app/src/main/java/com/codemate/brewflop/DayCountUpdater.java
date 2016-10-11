package com.codemate.brewflop;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v4.content.LocalBroadcastManager;

/**
 * Created by iiro on 22.9.2016.
 */
public class DayCountUpdater {
    private static final String PREFS_NAME = "day_counter_prefs";
    private static final String KEY_DAY_COUNT = "day_count";
    public static final String ACTION_DAY_COUNT_UPDATED = "com.codemate.brewflop.ACTION_DAY_COUNT_UPDATED";

    private final SharedPreferences preferences;
    private final Intent dayCountUpdatedEvent;
    private final LocalBroadcastManager broadcastManager;

    public DayCountUpdater(Context context) {
        this(context, LocalBroadcastManager.getInstance(context));
    }

    public DayCountUpdater(Context context, LocalBroadcastManager manager) {
        this.preferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        this.dayCountUpdatedEvent = new Intent(ACTION_DAY_COUNT_UPDATED);
        this.broadcastManager = manager;
    }

    public void increment() {
        int newDayCount = getDayCount() + 1;

        setCount(newDayCount);
    }

    public void setCount(int newDayCount) {
        preferences.edit()
                .putInt(KEY_DAY_COUNT, newDayCount)
                .apply();

        sendDayCountUpdatedEvent();
    }

    private void sendDayCountUpdatedEvent() {
        broadcastManager.sendBroadcast(dayCountUpdatedEvent);
    }

    public int getDayCount() {
        return preferences.getInt(KEY_DAY_COUNT, 0);
    }

    public void reset() {
        preferences.edit().clear().apply();

        sendDayCountUpdatedEvent();
    }
}
