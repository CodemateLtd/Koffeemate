package com.codemate.brewflop;

import android.app.AlarmManager;
import android.content.Context;
import android.content.Intent;

import com.codemate.brewflop.alarms.AlarmReceiver;
import com.codemate.brewflop.alarms.DayUpdateScheduler;

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
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.robolectric.Shadows.shadowOf;

@RunWith(RobolectricTestRunner.class)
public class DayUpdateSchedulerTest {
    private Context context;
    private ShadowAlarmManager shadowAlarmManager;

    private DayUpdateScheduler dayUpdateScheduler;

    @Before
    public void setUp() {
        context = RuntimeEnvironment.application.getApplicationContext();

        AlarmManager manager = (AlarmManager) RuntimeEnvironment.application.getSystemService(Context.ALARM_SERVICE);
        shadowAlarmManager = shadowOf(manager);

        dayUpdateScheduler = new DayUpdateScheduler(context);
    }

    @Test
    public void shouldScheduleAlarmEveryMorningAtSix() {
        assertNull(shadowAlarmManager.getNextScheduledAlarm());

        dayUpdateScheduler.setAlarm();

        ShadowAlarmManager.ScheduledAlarm scheduledAlarm = shadowAlarmManager.getNextScheduledAlarm();
        assertNotNull(scheduledAlarm);

        assertThat(scheduledAlarm.interval, is(Constants.DAY_UPDATE_INTERVAL));
        assertThat(scheduledAlarm.triggerAtTime, is(dayUpdateScheduler.getTriggerTime() + Constants.DAY_UPDATE_INTERVAL));
        assertThat(scheduledAlarm.type, is(AlarmManager.RTC_WAKEUP));
    }

    @Test
    public void shouldOnlyHaveOneAlarmAtTime() {
        dayUpdateScheduler.setAlarm();
        dayUpdateScheduler.setAlarm();
        dayUpdateScheduler.setAlarm();

        assertThat(shadowAlarmManager.getScheduledAlarms().size(), is(1));
    }

    @Test
    public void shouldCallAlarmReceiverWhenTimeIsUp() {
        Intent expectedIntent = new Intent(context, AlarmReceiver.class);

        dayUpdateScheduler.setAlarm();

        ShadowAlarmManager.ScheduledAlarm scheduledAlarm = shadowAlarmManager.getNextScheduledAlarm();
        ShadowPendingIntent shadowPendingIntent = shadowOf(scheduledAlarm.operation);

        assertTrue(shadowPendingIntent.isBroadcastIntent());
        assertThat(shadowPendingIntent.getSavedIntents().length, is(1));
        assertThat(shadowPendingIntent.getSavedIntents()[0].getComponent(), is(expectedIntent.getComponent()));
    }
}