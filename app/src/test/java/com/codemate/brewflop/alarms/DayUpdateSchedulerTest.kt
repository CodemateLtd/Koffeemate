package com.codemate.brewflop.alarms

import android.app.AlarmManager
import android.content.Context
import android.content.Intent
import com.codemate.brewflop.Constants
import junit.framework.Assert.*
import org.hamcrest.CoreMatchers
import org.hamcrest.CoreMatchers.`is`
import org.junit.Assert.assertThat
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.RuntimeEnvironment
import org.robolectric.Shadows.shadowOf
import org.robolectric.shadows.ShadowAlarmManager

@RunWith(RobolectricTestRunner::class)
class DayUpdateSchedulerTest {
    private lateinit var context: Context
    private lateinit var shadowAlarmManager: ShadowAlarmManager
    private lateinit var dayUpdateScheduler: DayUpdateScheduler

    @Before
    fun setUp() {
        context = RuntimeEnvironment.application.applicationContext

        val manager = RuntimeEnvironment.application.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        shadowAlarmManager = shadowOf(manager)

        dayUpdateScheduler = DayUpdateScheduler(context)
    }

    @Test
    fun shouldScheduleAlarmEveryMorningAtSix() {
        assertNull(shadowAlarmManager.nextScheduledAlarm)

        dayUpdateScheduler.setAlarm()

        val scheduledAlarm = shadowAlarmManager.nextScheduledAlarm
        assertNotNull(scheduledAlarm)

        assertThat(scheduledAlarm.interval, CoreMatchers.`is`(Constants.DAY_UPDATE_INTERVAL))
        assertThat(scheduledAlarm.triggerAtTime, `is`(dayUpdateScheduler.triggerTime + Constants.DAY_UPDATE_INTERVAL))
        assertThat(scheduledAlarm.type, `is`(AlarmManager.RTC_WAKEUP))
    }

    @Test
    fun shouldOnlyHaveOneAlarmAtTime() {
        dayUpdateScheduler.setAlarm()
        dayUpdateScheduler.setAlarm()
        dayUpdateScheduler.setAlarm()

        assertThat(shadowAlarmManager.scheduledAlarms.size, `is`(1))
    }

    @Test
    fun shouldCallAlarmReceiverWhenTimeIsUp() {
        val expectedIntent = Intent(context, AlarmReceiver::class.java)

        dayUpdateScheduler.setAlarm()

        val scheduledAlarm = shadowAlarmManager.nextScheduledAlarm
        val shadowPendingIntent = shadowOf(scheduledAlarm.operation)

        assertTrue(shadowPendingIntent.isBroadcastIntent)
        assertThat(shadowPendingIntent.savedIntents.size, `is`(1))
        assertThat(shadowPendingIntent.savedIntents[0].component, `is`(expectedIntent.component))
    }
}