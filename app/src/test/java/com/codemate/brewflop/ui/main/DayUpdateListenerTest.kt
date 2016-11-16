package com.codemate.brewflop.ui.main


import android.content.Intent
import android.support.v4.content.LocalBroadcastManager
import com.codemate.brewflop.DayCountUpdater
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mockito.*
import org.robolectric.RobolectricTestRunner
import org.robolectric.RuntimeEnvironment

@RunWith(RobolectricTestRunner::class)
class DayUpdateListenerTest {
    private lateinit var broadcastManager: LocalBroadcastManager
    private lateinit var onDayChangedListener: DayUpdateListener.OnDayChangedListener
    private lateinit var dayUpdateListener: DayUpdateListener

    @Before
    fun setUp() {
        val context = RuntimeEnvironment.application.applicationContext

        broadcastManager = LocalBroadcastManager.getInstance(context)
        onDayChangedListener = mock(DayUpdateListener.OnDayChangedListener::class.java)
        dayUpdateListener = DayUpdateListener(broadcastManager, onDayChangedListener)
    }

    @Test
    fun shouldCallListenerWhenListeningForDayChanges() {
        dayUpdateListener.listenForDayChanges()

        sendDayCountUpdatedBroadcast()
        sendDayCountUpdatedBroadcast()
        sendDayCountUpdatedBroadcast()

        verify(onDayChangedListener, times(3)).onDayChanged()
    }

    @Test
    fun shouldNotCallListenerWhenNotListeningForDayChanges() {
        dayUpdateListener.stopListeningForDayChanges()

        sendDayCountUpdatedBroadcast()
        sendDayCountUpdatedBroadcast()
        sendDayCountUpdatedBroadcast()

        verifyZeroInteractions(onDayChangedListener)
    }

    private fun sendDayCountUpdatedBroadcast() {
        broadcastManager.sendBroadcast(Intent(DayCountUpdater.ACTION_DAY_COUNT_UPDATED))
    }
}
