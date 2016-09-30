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
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

/**
 * Created by iiro on 22.9.2016.
 */
@RunWith(RobolectricTestRunner.class)
public class DayCounterTest {
    private LocalBroadcastManager broadcastManager;
    private DayCounter dayCounter;

    private ArgumentCaptor<Intent> intentCaptor;

    @Before
    public void setUp() {
        broadcastManager = mock(LocalBroadcastManager.class);
        dayCounter = new DayCounter(
                RuntimeEnvironment.application.getApplicationContext(),
                broadcastManager
        );

        intentCaptor = ArgumentCaptor.forClass(Intent.class);
    }

    @After
    public void tearDown() {
        dayCounter.reset();
    }

    @Test
    public void testIncrement() {
        assertThat(dayCounter.getDayCount(), is(0));

        dayCounter.increment();
        dayCounter.increment();
        assertThat(dayCounter.getDayCount(), is(2));

        verify(broadcastManager, times(2)).sendBroadcast(intentCaptor.capture());
        assertThat(intentCaptor.getValue().getAction(), is(DayCounter.ACTION_DAY_COUNT_UPDATED));
    }

    @Test
    public void testClear() {
        dayCounter.increment();
        dayCounter.increment();
        assertThat(dayCounter.getDayCount(), is(2));

        dayCounter.reset();
        assertThat(dayCounter.getDayCount(), is(0));

        verify(broadcastManager, times(3)).sendBroadcast(intentCaptor.capture());
        assertThat(intentCaptor.getValue().getAction(), is(DayCounter.ACTION_DAY_COUNT_UPDATED));
    }
}