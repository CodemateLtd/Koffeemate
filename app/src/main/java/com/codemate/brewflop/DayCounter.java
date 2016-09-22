package com.codemate.brewflop;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by iiro on 22.9.2016.
 */
public class DayCounter {
    private static final String PREFS_NAME = "day_counter_prefs";
    private static final String KEY_DAY_COUNT = "day_count";

    private final SharedPreferences preferences;

    public DayCounter(Context context) {
        this.preferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
    }

    public void increment() {
        int newDayCount = getDayCount() + 1;

        preferences.edit()
                .putInt(KEY_DAY_COUNT, newDayCount)
                .apply();
    }

    public int getDayCount() {
        return preferences.getInt(KEY_DAY_COUNT, 0);
    }

    public void clear() {
        preferences.edit().clear().apply();
    }
}
