package com.codemate.brewflop.ui.main;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v4.content.LocalBroadcastManager;

import com.codemate.brewflop.DayCountUpdater;

public class DayUpdateListener {
    private final LocalBroadcastManager broadcastManager;
    private final OnDayChangedListener listener;
    private IntentFilter dayCountUpdatedFilter;
    private BroadcastReceiver dayCountUpdatedReceiver;

    public DayUpdateListener(LocalBroadcastManager manager, OnDayChangedListener listener) {
        this.broadcastManager = manager;
        this.listener = listener;
    }

    public void listenForDayChanges() {
        if (dayCountUpdatedFilter == null || dayCountUpdatedReceiver == null) {
            dayCountUpdatedFilter = new IntentFilter(DayCountUpdater.ACTION_DAY_COUNT_UPDATED);
            dayCountUpdatedReceiver = new BroadcastReceiver() {
                @Override
                public void onReceive(Context context, Intent intent) {
                    listener.onDayChanged();
                }
            };
        }

        broadcastManager.registerReceiver(dayCountUpdatedReceiver, dayCountUpdatedFilter);
    }

    public void stopListeningForDayChanges() {
        broadcastManager.unregisterReceiver(dayCountUpdatedReceiver);
    }

    public interface OnDayChangedListener {
        void onDayChanged();
    }
}