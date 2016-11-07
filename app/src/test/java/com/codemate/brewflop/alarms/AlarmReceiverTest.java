package com.codemate.brewflop.alarms;

import android.content.Context;
import android.content.Intent;

import com.codemate.brewflop.DayCountUpdater;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;

import java.util.Calendar;

import static junit.framework.Assert.assertNotNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyZeroInteractions;
import static org.mockito.Mockito.when;

/**
 * Created by iiro on 22.9.2016.
 */
@RunWith(RobolectricTestRunner.class)
public class AlarmReceiverTest {
    private Context context;
    private AlarmReceiver alarmReceiver;
    private DayCountUpdater dayCountUpdater;
    private Calendar calendar;

    @Before
    public void setUp() {
        context = RuntimeEnvironment.application.getApplicationContext();

        dayCountUpdater = mock(DayCountUpdater.class);
        calendar = mock(Calendar.class);

        alarmReceiver = new AlarmReceiver();
        alarmReceiver.dayCountUpdater = dayCountUpdater;
        alarmReceiver.calendar = calendar;
    }

    @Test
    public void shouldUseRealDayCountUpdaterIfFieldNotInjected() {
        alarmReceiver.dayCountUpdater = null;
        alarmReceiver.onReceive(context, new Intent());

        assertNotNull(alarmReceiver.dayCountUpdater);
    }

    @Test
    public void shouldUpdateDayCountBetweenMondayToFriday() {
        for (int dayOfWeek = Calendar.MONDAY; dayOfWeek <= Calendar.FRIDAY; dayOfWeek++) {
            when(calendar.get(Calendar.DAY_OF_WEEK)).thenReturn(dayOfWeek);
            alarmReceiver.onReceive(context, new Intent());
        }

        verify(dayCountUpdater, times(5)).increment();
    }

    @Test
    public void shouldNotUpdateDayCountWhenSaturdayOrSunday() {
        when(calendar.get(Calendar.DAY_OF_WEEK)).thenReturn(Calendar.SATURDAY);
        alarmReceiver.onReceive(context, new Intent());

        when(calendar.get(Calendar.DAY_OF_WEEK)).thenReturn(Calendar.SUNDAY);
        alarmReceiver.onReceive(context, new Intent());

        verifyZeroInteractions(dayCountUpdater);
    }
}