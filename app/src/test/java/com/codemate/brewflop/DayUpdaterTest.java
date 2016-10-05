package com.codemate.brewflop;

import android.app.AlarmManager;
import android.content.Context;
import android.content.Intent;

import com.codemate.brewflop.alarms.AlarmReceiver;
import com.codemate.brewflop.alarms.DayUpdater;

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
public class DayUpdaterTest {
    private Context context;
    private ShadowAlarmManager shadowAlarmManager;

    private DayUpdater dayUpdater;

    @Before
    public void setUp() {
        context = RuntimeEnvironment.application.getApplicationContext();

        AlarmManager manager = (AlarmManager) RuntimeEnvironment.application.getSystemService(Context.ALARM_SERVICE);
        shadowAlarmManager = shadowOf(manager);

        dayUpdater = new DayUpdater(context);
    }

    @Test
    public void shouldScheduleAlarmEveryMorningAtSix() {
        assertNull(shadowAlarmManager.getNextScheduledAlarm());

        dayUpdater.setAlarm();

        ShadowAlarmManager.ScheduledAlarm scheduledAlarm = shadowAlarmManager.getNextScheduledAlarm();
        assertNotNull(scheduledAlarm);

        assertThat(scheduledAlarm.interval, is(Constants.DAY_UPDATE_INTERVAL));
        assertThat(scheduledAlarm.triggerAtTime, is(dayUpdater.getTriggerTime() + Constants.DAY_UPDATE_INTERVAL));
        assertThat(scheduledAlarm.type, is(AlarmManager.RTC_WAKEUP));
    }

    @Test
    public void shouldOnlyHaveOneAlarmAtTime() {
        dayUpdater.setAlarm();
        dayUpdater.setAlarm();
        dayUpdater.setAlarm();

        assertThat(shadowAlarmManager.getScheduledAlarms().size(), is(1));
    }

    @Test
    public void shouldCallAlarmReceiverWhenTimeIsUp() {
        Intent expectedIntent = new Intent(context, AlarmReceiver.class);

        dayUpdater.setAlarm();

        ShadowAlarmManager.ScheduledAlarm scheduledAlarm = shadowAlarmManager.getNextScheduledAlarm();
        ShadowPendingIntent shadowPendingIntent = shadowOf(scheduledAlarm.operation);

        assertTrue(shadowPendingIntent.isBroadcastIntent());
        assertThat(shadowPendingIntent.getSavedIntents().length, is(1));
        assertThat(shadowPendingIntent.getSavedIntents()[0].getComponent(), is(expectedIntent.getComponent()));
    }
}