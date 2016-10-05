package com.codemate.brewflop;

import android.content.Context;
import android.content.Intent;

import com.codemate.brewflop.alarms.AlarmReceiver;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

/**
 * Created by iiro on 22.9.2016.
 */
@RunWith(RobolectricTestRunner.class)
public class AlarmReceiverTest {
    private Context context;
    private AlarmReceiver alarmReceiver;
    private DayCounter dayCounter;

    @Before
    public void setUp() {
        context = RuntimeEnvironment.application.getApplicationContext();

        dayCounter = mock(DayCounter.class);
        alarmReceiver = new AlarmReceiver();
        alarmReceiver.dayCounter = dayCounter;
    }

    @Test
    public void testOnReceive() {
        alarmReceiver.onReceive(context, new Intent());

        verify(dayCounter).increment();
    }
}