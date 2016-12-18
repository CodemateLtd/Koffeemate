package com.codemate.brewflop.ui.main

import android.content.SharedPreferences
import android.os.Handler
import com.codemate.brewflop.BuildConfig
import com.codemate.brewflop.SynchronousExecutorService
import com.codemate.brewflop.data.BrewingProgressUpdater
import com.codemate.brewflop.data.local.CoffeePreferences
import com.codemate.brewflop.data.local.CoffeeEventRepository
import com.codemate.brewflop.data.local.models.CoffeeBrewingEvent
import com.codemate.brewflop.data.network.SlackApi
import com.codemate.brewflop.data.network.SlackService
import com.nhaarman.mockito_kotlin.*
import okhttp3.Dispatcher
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.hamcrest.Matchers.containsString
import org.hamcrest.core.IsEqual.equalTo
import org.junit.After
import org.junit.Assert.assertThat
import org.junit.Before
import org.junit.Test

class MainPresenterTest {
    val CHANNEL_NAME = "fake-channel"

    lateinit var coffeePreferences: CoffeePreferences
    lateinit var mockCoffeeEventRepository: CoffeeEventRepository
    lateinit var mockHandler: Handler
    lateinit var updater: BrewingProgressUpdater
    lateinit var mockServer: MockWebServer
    lateinit var slackApi: SlackApi

    lateinit var presenter: MainPresenter
    lateinit var view: MainView

    @Before
    fun setUp() {
        coffeePreferences = mock<CoffeePreferences>()
        coffeePreferences.preferences = mock<SharedPreferences>()
        whenever(coffeePreferences.getCoffeeAnnouncementChannel()).thenReturn(CHANNEL_NAME)

        mockCoffeeEventRepository = mock<CoffeeEventRepository>()

        mockHandler = mock<Handler>()
        updater = BrewingProgressUpdater(9, 3)
        updater.updateHandler = mockHandler

        mockServer = MockWebServer()
        mockServer.start()

        slackApi = SlackService.getApi(Dispatcher(SynchronousExecutorService()), mockServer.url("/"))
        presenter = MainPresenter(coffeePreferences, mockCoffeeEventRepository, updater, slackApi)
        view = mock<MainView>()

        presenter.attachView(view)
    }

    @After
    fun tearDown() {
        mockServer.shutdown()
    }

    @Test
    fun startDelayedCoffeeAnnouncement_WhenChannelNameNotSet_AndIsNotUpdatingProgress_InformsView() {
        whenever(coffeePreferences.getCoffeeAnnouncementChannel()).thenReturn("")
        updater.isUpdating = false

        presenter.startDelayedCoffeeAnnouncement("")

        verify(view, times(1)).noChannelNameSet()
        verifyNoMoreInteractions(view)
        verifyZeroInteractions(mockCoffeeEventRepository)
    }

    @Test
    fun startDelayedCoffeeAnnouncement_WhenUpdaterNotUpdating_TellsViewNewCoffeeIsComing() {
        presenter.startDelayedCoffeeAnnouncement("")

        verify(view, times(1)).newCoffeeIsComing()
        verify(view, times(1)).updateCoffeeProgress(0)
        verifyZeroInteractions(mockCoffeeEventRepository)
    }

    @Test
    fun startDelayedCoffeeAnnouncement_WhenUpdaterAlreadyUpdating_ShowsCancelCoffeeProgressPrompt() {
        updater.isUpdating = true
        presenter.startDelayedCoffeeAnnouncement("")

        verify(view, times(1)).showCancelCoffeeProgressPrompt()
        verifyZeroInteractions(mockCoffeeEventRepository)
    }

    @Test
    fun startDelayedCoffeeAnnouncement_WhenRunToEnd_UpdatesCoffeeProgressAndPostsToSlack() {
        mockServer.enqueue(MockResponse().setBody(""))
        presenter.startDelayedCoffeeAnnouncement("A happy message about coffee status")

        updater.run()
        updater.run()
        updater.run()

        inOrder(view, mockCoffeeEventRepository) {
            verify(view).updateCoffeeProgress(0)
            verify(view).updateCoffeeProgress(33)
            verify(view).updateCoffeeProgress(67)
            verify(view).resetCoffeeViewStatus()
            verify(mockCoffeeEventRepository).recordBrewingEvent()
        }

        val apiRequest = mockServer.takeRequest()
        assertThat(apiRequest.path, equalTo("/chat.postMessage"))

        val requestBody = apiRequest.body.readUtf8()
        assertThat(requestBody, containsString("token=${BuildConfig.SLACK_AUTH_TOKEN}"))
        assertThat(requestBody, containsString("channel=$CHANNEL_NAME"))
        assertThat(requestBody, containsString("text=A%20happy%20message%20about%20coffee%20status"))
        assertThat(requestBody, containsString("as_user=false"))
    }

    @Test
    fun updateLastBrewingEventTime_WhenNoCoffeeBrewingEvents_DoesNothing() {
        whenever(mockCoffeeEventRepository.getLastBrewingEvent()).thenReturn(null)
        presenter.updateLastBrewingEventTime()

        verifyZeroInteractions(view)
    }

    @Test
    fun testupdateLastBrewingEventTime_WhenHasCoffeeBrewingEvents_ShowsLastInUI() {
        val lastEvent = CoffeeBrewingEvent(time = System.currentTimeMillis())

        whenever(mockCoffeeEventRepository.getLastBrewingEvent()).thenReturn(lastEvent)
        presenter.updateLastBrewingEventTime()

        verify(view).setLastBrewingEvent(lastEvent)
    }

    @Test
    fun cancelCoffeeCountDown_ResetsUpdaterAndUpdatesView() {
        presenter.cancelCoffeeCountDown()

        verify(view).updateCoffeeProgress(0)
        verify(view).resetCoffeeViewStatus()

        verify(mockHandler).removeCallbacks(updater)
        verifyZeroInteractions(mockCoffeeEventRepository)
    }
}