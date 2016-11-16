package com.codemate.brewflop.alarms

import android.content.Context
import android.content.Intent
import org.junit.Assert.assertNotNull
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mockito.mock
import org.mockito.Mockito.verify
import org.robolectric.RobolectricTestRunner
import org.robolectric.RuntimeEnvironment

/**
 * Created by iiro on 22.9.2016.
 */
@RunWith(RobolectricTestRunner::class)
class BootReceiverTest {
    private lateinit var context: Context
    private lateinit var bootReceiver: BootReceiver
    private lateinit var dayUpdateScheduler: DayUpdateScheduler

    @Before
    fun setUp() {
        context = RuntimeEnvironment.application.applicationContext
        bootReceiver = BootReceiver()
        dayUpdateScheduler = mock(DayUpdateScheduler::class.java)
        bootReceiver.dayUpdateScheduler = dayUpdateScheduler
    }

    @Test
    fun shouldUseRealDayUpdateSchedulerIfFieldNotInjected() {
        bootReceiver.dayUpdateScheduler = null
        bootReceiver.onReceive(context, Intent())

        assertNotNull(bootReceiver.dayUpdateScheduler)
    }

    @Test
    fun shouldSetRepeatingAlarmOnBoot() {
        bootReceiver.onReceive(context, Intent(Intent.ACTION_BOOT_COMPLETED))

        verify<DayUpdateScheduler>(dayUpdateScheduler).setAlarm()
    }
}
