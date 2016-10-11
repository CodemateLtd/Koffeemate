package com.codemate.brewflop;

import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

/**
 * Created by iiro on 22.9.2016.
 */
@RunWith(RobolectricTestRunner.class)
public class DayCountUpdaterTest {
    private LocalBroadcastManager broadcastManager;
    private DayCountUpdater dayCountUpdater;

    private ArgumentCaptor<Intent> intentCaptor;

    @Before
    public void setUp() {
        broadcastManager = mock(LocalBroadcastManager.class);
        dayCountUpdater = new DayCountUpdater(
                RuntimeEnvironment.application.getApplicationContext(),
                broadcastManager
        );

        intentCaptor = new ArgumentCaptor<>();
    }

    @After
    public void tearDown() {
        dayCountUpdater.reset();
    }

    @Test
    public void shouldPersistAndNotifyAboutCountWhenIncremented() {
        assertThat(dayCountUpdater.getDayCount(), is(0));

        dayCountUpdater.increment();
        dayCountUpdater.increment();
        assertThat(dayCountUpdater.getDayCount(), is(2));

        verify(broadcastManager, times(2)).sendBroadcast(intentCaptor.capture());
        assertThat(intentCaptor.getValue().getAction(), is(DayCountUpdater.ACTION_DAY_COUNT_UPDATED));
    }

    @Test
    public void shouldClearCountAndNotifyWhenCleared() {
        dayCountUpdater.increment();
        dayCountUpdater.increment();
        assertThat(dayCountUpdater.getDayCount(), is(2));

        dayCountUpdater.reset();
        assertThat(dayCountUpdater.getDayCount(), is(0));

        verify(broadcastManager, times(3)).sendBroadcast(intentCaptor.capture());
        assertThat(intentCaptor.getValue().getAction(), is(DayCountUpdater.ACTION_DAY_COUNT_UPDATED));
    }
}