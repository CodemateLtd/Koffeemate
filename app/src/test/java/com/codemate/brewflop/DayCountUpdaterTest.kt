package com.codemate.brewflop

import android.content.Intent
import android.support.v4.content.LocalBroadcastManager
import org.hamcrest.Matchers.`is`
import org.junit.After
import org.junit.Assert.assertThat
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.ArgumentCaptor
import org.mockito.Mockito.*
import org.robolectric.RobolectricTestRunner
import org.robolectric.RuntimeEnvironment

/**
 * Created by iiro on 22.9.2016.
 */
@RunWith(RobolectricTestRunner::class)
class DayCountUpdaterTest {
    private lateinit var broadcastManager: LocalBroadcastManager
    private lateinit var dayCountUpdater: DayCountUpdater
    private lateinit var intentCaptor: ArgumentCaptor<Intent>

    @Before
    fun setUp() {
        broadcastManager = mock(LocalBroadcastManager::class.java)
        dayCountUpdater = DayCountUpdater(
                RuntimeEnvironment.application.applicationContext,
                broadcastManager
        )

        intentCaptor = ArgumentCaptor.forClass(Intent::class.java)
    }

    @After
    fun tearDown() {
        dayCountUpdater.reset()
    }

    @Test
    fun shouldPersistAndNotifyAboutCountWhenIncremented() {
        assertThat(dayCountUpdater.dayCount, `is`(0))

        dayCountUpdater.increment()
        dayCountUpdater.increment()
        assertThat(dayCountUpdater.dayCount, `is`(2))

        verify<LocalBroadcastManager>(broadcastManager, times(2)).sendBroadcast(intentCaptor.capture())
        assertThat(intentCaptor.value.action, `is`(DayCountUpdater.ACTION_DAY_COUNT_UPDATED))
    }

    @Test
    fun shouldClearCountAndNotifyWhenCleared() {
        dayCountUpdater.increment()
        dayCountUpdater.increment()
        assertThat(dayCountUpdater.dayCount, `is`(2))

        dayCountUpdater.reset()
        assertThat(dayCountUpdater.dayCount, `is`(0))

        verify<LocalBroadcastManager>(broadcastManager, times(3)).sendBroadcast(intentCaptor.capture())
        assertThat(intentCaptor.value.action, `is`(DayCountUpdater.ACTION_DAY_COUNT_UPDATED))
    }
}