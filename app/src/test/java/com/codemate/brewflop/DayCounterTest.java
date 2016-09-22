package com.codemate.brewflop;

import android.app.AlarmManager;
import android.content.Context;
import android.content.Intent;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.shadows.ShadowAlarmManager;
import org.robolectric.shadows.ShadowPendingIntent;

import static junit.framework.Assert.assertNotNull;
import static junit.framework.Assert.assertNull;
import static junit.framework.Assert.assertTrue;
import static org.hamcrest.CoreMatchers.both;
import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.lessThan;
import static org.junit.Assert.*;
import static org.robolectric.Shadows.shadowOf;

@RunWith(RobolectricTestRunner.class)
public class DayCounterTest {
    private Context context;
    private ShadowAlarmManager shadowAlarmManager;

    private DayCounter dayCounter;

    @Before
    public void setUp() {
        context = RuntimeEnvironment.application.getApplicationContext();

        AlarmManager manager = (AlarmManager) RuntimeEnvironment.application.getSystemService(Context.ALARM_SERVICE);
        shadowAlarmManager = shadowOf(manager);

        dayCounter = new DayCounter(context);
    }

    @Test
    public void shouldScheduleAlarmEveryMorningAtSix() {
        assertNull(shadowAlarmManager.getNextScheduledAlarm());

        dayCounter.setAlarmIfNotExists();

        ShadowAlarmManager.ScheduledAlarm scheduledAlarm = shadowAlarmManager.getNextScheduledAlarm();
        assertNotNull(scheduledAlarm);

        assertThat(scheduledAlarm.interval, is(AlarmManager.INTERVAL_DAY));
        assertThat(scheduledAlarm.triggerAtTime, is(dayCounter.getTriggerTime()));
        assertThat(scheduledAlarm.type, is(AlarmManager.RTC_WAKEUP));
    }

    @Test
    public void shouldOnlyHaveOneAlarmAtTime() {
        dayCounter.setAlarmIfNotExists();
        dayCounter.setAlarmIfNotExists();
        dayCounter.setAlarmIfNotExists();

        assertThat(shadowAlarmManager.getScheduledAlarms().size(), is(1));
    }

    @Test
    public void shouldCallAlarmReceiverWhenTimeIsUp() {
        Intent expectedIntent = new Intent(context, AlarmReceiver.class);

        dayCounter.setAlarmIfNotExists();

        ShadowAlarmManager.ScheduledAlarm scheduledAlarm = shadowAlarmManager.getNextScheduledAlarm();
        ShadowPendingIntent shadowPendingIntent = shadowOf(scheduledAlarm.operation);

        assertTrue(shadowPendingIntent.isBroadcastIntent());
        assertThat(shadowPendingIntent.getSavedIntents().length, is(1));
        assertThat(shadowPendingIntent.getSavedIntents()[0].getComponent(), is(expectedIntent.getComponent()));
    }
}