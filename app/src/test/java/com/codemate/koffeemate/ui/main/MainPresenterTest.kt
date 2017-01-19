package com.codemate.koffeemate.ui.main

import android.content.SharedPreferences
import android.os.Handler
import com.codemate.koffeemate.common.BrewingProgressUpdater
import com.codemate.koffeemate.common.ScreenSaver
import com.codemate.koffeemate.data.local.CoffeeEventRepository
import com.codemate.koffeemate.data.local.CoffeePreferences
import com.codemate.koffeemate.data.local.models.CoffeeBrewingEvent
import com.nhaarman.mockito_kotlin.*
import org.junit.Before
import org.junit.Test
import org.mockito.Mock
import org.mockito.MockitoAnnotations

class MainPresenterTest {
    val CHANNEL_NAME = "fake-channel"

    @Mock
    lateinit var coffeePreferences: CoffeePreferences

    @Mock
    lateinit var mockCoffeeEventRepository: CoffeeEventRepository

    @Mock
    lateinit var mockHandler: Handler

    @Mock
    lateinit var view: MainView

    @Mock
    lateinit var mockScreenSaver: ScreenSaver

    @Mock
    lateinit var mockSendCoffeeAnnouncementUseCase: SendCoffeeAnnouncementUseCase

    @Mock
    lateinit var mockUpdater: BrewingProgressUpdater

    lateinit var presenter: MainPresenter

    @Before
    fun setUp() {
        MockitoAnnotations.initMocks(this)

        coffeePreferences.preferences = mock<SharedPreferences>()
        whenever(coffeePreferences.getAccidentChannel()).thenReturn(CHANNEL_NAME)
        whenever(coffeePreferences.getCoffeeAnnouncementChannel()).thenReturn(CHANNEL_NAME)

        mockUpdater.updateHandler = mockHandler
        whenever(mockHandler.removeCallbacks(any())).then {
            // No-op
        }
        whenever(mockUpdater.reset()).then {
            // No-op
        }

        presenter = MainPresenter(
                coffeePreferences,
                mockCoffeeEventRepository,
                mockUpdater,
                mockSendCoffeeAnnouncementUseCase
        )
        presenter.attachView(view)
        presenter.setScreenSaver(mockScreenSaver)
    }

    @Test
    fun startDelayedCoffeeAnnouncement_DefersScreenSaver() {
        presenter.startDelayedCoffeeAnnouncement("")
        verify(mockScreenSaver).defer()
    }

    @Test
    fun launchUserSelector_DefersScreenSaver() {
        presenter.launchAccidentReportingScreen()
        verify(mockScreenSaver).defer()
    }

    @Test
    fun launchUserSelector_WhenNoAccidentChannelSet_InformsView() {
        whenever(coffeePreferences.getAccidentChannel()).thenReturn("")
        presenter.launchAccidentReportingScreen()

        verify(view).showNoAccidentChannelSetError()
        verifyNoMoreInteractions(view)
    }

    @Test
    fun launchUserSelector_WhenAccidentChannelSet_LaunchesUserSelector() {
        whenever(coffeePreferences.isAccidentChannelSet()).thenReturn(true)
        presenter.launchAccidentReportingScreen()

        verify(view).launchAccidentReportingScreen()
        verifyNoMoreInteractions(view)
    }

    @Test
    fun startDelayedCoffeeAnnouncement_WhenChannelNameNotSet_AndIsNotUpdatingProgress_InformsView() {
        whenever(coffeePreferences.getCoffeeAnnouncementChannel()).thenReturn("")
        presenter.startDelayedCoffeeAnnouncement("")

        verify(view, times(1)).showNoAnnouncementChannelSetError()
        verifyNoMoreInteractions(view)
        verifyZeroInteractions(mockCoffeeEventRepository)
    }

    @Test
    fun startDelayedCoffeeAnnouncement_WhenUpdaterNotUpdating_TellsViewNewCoffeeIsComing() {
        whenever(coffeePreferences.isCoffeeAnnouncementChannelSet()).thenReturn(true)
        presenter.startDelayedCoffeeAnnouncement("")

        verify(view).showNewCoffeeIsComing()
        verifyNoMoreInteractions(view)
        verifyZeroInteractions(mockCoffeeEventRepository)
    }

    @Test
    fun startDelayedCoffeeAnnouncement_WhenUpdaterAlreadyUpdating_ShowsCancelCoffeeProgressPrompt() {
        mockUpdater.isUpdating = true
        presenter.startDelayedCoffeeAnnouncement("")

        verify(view, times(1)).showCancelCoffeeProgressPrompt()
        verifyZeroInteractions(mockCoffeeEventRepository)
    }

    @Test
    fun updateLastBrewingEventTime_WhenNoCoffeeBrewingEvents_DoesNothing() {
        whenever(mockCoffeeEventRepository.getLastBrewingEvent()).thenReturn(null)
        presenter.updateLastBrewingEventTime()

        verifyZeroInteractions(view)
    }

    @Test
    fun updateLastBrewingEventTime_WhenHasCoffeeBrewingEvents_ShowsLastInUI() {
        val lastEvent = CoffeeBrewingEvent(time = System.currentTimeMillis())

        whenever(mockCoffeeEventRepository.getLastBrewingEvent()).thenReturn(lastEvent)
        presenter.updateLastBrewingEventTime()

        verify(view).updateLastBrewingEvent(lastEvent)
    }

    @Test
    fun cancelCoffeeCountDown_ResetsUpdaterAndUpdatesView() {
        presenter.cancelCoffeeCountDown()

        verify(view).updateCoffeeProgress(0)
        verify(view).resetCoffeeViewStatus()

        verify(mockHandler).removeCallbacks(mockUpdater)
        verifyZeroInteractions(mockCoffeeEventRepository)
    }
}