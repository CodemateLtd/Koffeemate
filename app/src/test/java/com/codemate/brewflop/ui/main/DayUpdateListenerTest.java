package com.codemate.brewflop.ui.main;


import android.content.Context;
import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;

import com.codemate.brewflop.DayCountUpdater;
import com.codemate.brewflop.ui.main.DayUpdateListener;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyZeroInteractions;

@RunWith(RobolectricTestRunner.class)
public class DayUpdateListenerTest {
    private LocalBroadcastManager broadcastManager;
    private DayUpdateListener.OnDayChangedListener onDayChangedListener;
    private DayUpdateListener dayUpdateListener;

    @Before
    public void setUp() {
        Context context = RuntimeEnvironment.application.getApplicationContext();

        broadcastManager = LocalBroadcastManager.getInstance(context);
        onDayChangedListener = mock(DayUpdateListener.OnDayChangedListener.class);
        dayUpdateListener = new DayUpdateListener(broadcastManager, onDayChangedListener);
    }

    @Test
    public void shouldCallListenerWhenListeningForDayChanges() {
        dayUpdateListener.listenForDayChanges();

        sendDayCountUpdatedBroadcast();
        sendDayCountUpdatedBroadcast();
        sendDayCountUpdatedBroadcast();

        verify(onDayChangedListener, times(3)).onDayChanged();
    }

    @Test
    public void shouldNotCallListenerWhenNotListeningForDayChanges() {
        dayUpdateListener.stopListeningForDayChanges();

        sendDayCountUpdatedBroadcast();
        sendDayCountUpdatedBroadcast();
        sendDayCountUpdatedBroadcast();

        verifyZeroInteractions(onDayChangedListener);
    }

    private void sendDayCountUpdatedBroadcast() {
        broadcastManager.sendBroadcast(new Intent(DayCountUpdater.ACTION_DAY_COUNT_UPDATED));
    }
}
