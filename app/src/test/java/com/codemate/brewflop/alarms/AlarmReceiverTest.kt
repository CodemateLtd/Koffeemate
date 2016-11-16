package com.codemate.brewflop.alarms

import android.content.Context
import android.content.Intent

import com.codemate.brewflop.DayCountUpdater

import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.RuntimeEnvironment

import java.util.Calendar

import junit.framework.Assert.assertNotNull
import org.mockito.Mockito.mock
import org.mockito.Mockito.times
import org.mockito.Mockito.verify
import org.mockito.Mockito.verifyZeroInteractions
import org.mockito.Mockito.`when`

/**
 * Created by iiro on 22.9.2016.
 */
@RunWith(RobolectricTestRunner::class)
class AlarmReceiverTest {
    private lateinit var context: Context
    private lateinit var alarmReceiver: AlarmReceiver
    private lateinit var dayCountUpdater: DayCountUpdater
    private lateinit var calendar: Calendar

    @Before
    fun setUp() {
        context = RuntimeEnvironment.application.applicationContext

        dayCountUpdater = mock(DayCountUpdater::class.java)
        calendar = mock(Calendar::class.java)

        alarmReceiver = AlarmReceiver()
        alarmReceiver.dayCountUpdater = dayCountUpdater
        alarmReceiver.calendar = calendar
    }

    @Test
    fun shouldUseRealDayCountUpdaterIfFieldNotInjected() {
        alarmReceiver.dayCountUpdater = null
        alarmReceiver.onReceive(context, Intent())

        assertNotNull(alarmReceiver.dayCountUpdater)
    }

    @Test
    fun shouldUpdateDayCountBetweenMondayToFriday() {
        for (dayOfWeek in Calendar.MONDAY..Calendar.FRIDAY) {
            `when`(calendar.get(Calendar.DAY_OF_WEEK)).thenReturn(dayOfWeek)
            alarmReceiver.onReceive(context, Intent())
        }

        verify<DayCountUpdater>(dayCountUpdater, times(5)).increment()
    }

    @Test
    fun shouldNotUpdateDayCountWhenSaturdayOrSunday() {
        `when`(calendar.get(Calendar.DAY_OF_WEEK)).thenReturn(Calendar.SATURDAY)
        alarmReceiver.onReceive(context, Intent())

        `when`(calendar.get(Calendar.DAY_OF_WEEK)).thenReturn(Calendar.SUNDAY)
        alarmReceiver.onReceive(context, Intent())

        verifyZeroInteractions(dayCountUpdater)
    }
}