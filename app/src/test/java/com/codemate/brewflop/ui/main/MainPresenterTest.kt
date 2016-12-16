package com.codemate.brewflop.ui.main

import android.content.SharedPreferences
import android.os.Handler
import com.codemate.brewflop.BuildConfig
import com.codemate.brewflop.SynchronousExecutorService
import com.codemate.brewflop.data.BrewingProgressUpdater
import com.codemate.brewflop.data.local.CoffeePreferences
import com.codemate.brewflop.data.local.CoffeeStatisticLogger
import com.codemate.brewflop.data.network.SlackApi
import com.codemate.brewflop.data.network.SlackService
import okhttp3.Dispatcher
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.hamcrest.Matchers.containsString
import org.hamcrest.core.IsEqual.equalTo
import org.junit.After
import org.junit.Assert.assertThat
import org.junit.Before
import org.junit.Test
import org.mockito.Mockito.*

class MainPresenterTest {
    private val CHANNEL_NAME = "fake-channel"

    private lateinit var coffeePreferences: CoffeePreferences
    private lateinit var mockStatisticLogger: CoffeeStatisticLogger
    private lateinit var mockHandler: Handler
    private lateinit var updater: BrewingProgressUpdater
    private lateinit var mockServer: MockWebServer
    private lateinit var slackApi: SlackApi

    private lateinit var presenter: MainPresenter
    private lateinit var view: MainView

    @Before
    fun setUp() {
        coffeePreferences = mock(CoffeePreferences::class.java)
        coffeePreferences.preferences = mock(SharedPreferences::class.java)
        `when`(coffeePreferences.getChannelName()).thenReturn(CHANNEL_NAME)

        mockStatisticLogger = mock(CoffeeStatisticLogger::class.java)

        mockHandler = mock(Handler::class.java)
        updater = BrewingProgressUpdater(9, 3)
        updater.updateHandler = mockHandler

        mockServer = MockWebServer()
        mockServer.start()

        slackApi = SlackService.getApi(Dispatcher(SynchronousExecutorService()), mockServer.url("/"))
        presenter = MainPresenter(coffeePreferences, mockStatisticLogger, updater, slackApi)
        view = mock(MainView::class.java)

        presenter.attachView(view)
    }

    @After
    fun tearDown() {
        mockServer.shutdown()
    }

    @Test
    fun startDelayedCoffeeAnnouncement_WhenChannelNameNotSet_AndIsNotUpdatingProgress_InformsView() {
        `when`(coffeePreferences.getChannelName()).thenReturn("")
        updater.isUpdating = false

        presenter.startDelayedCoffeeAnnouncement("")

        verify(view, times(1)).noChannelNameSet()
        verifyNoMoreInteractions(view)
        verifyZeroInteractions(mockStatisticLogger)
    }

    @Test
    fun startDelayedCoffeeAnnouncement_WhenUpdaterNotUpdating_TellsViewNewCoffeeIsComing() {
        presenter.startDelayedCoffeeAnnouncement("")

        verify(view, times(1)).newCoffeeIsComing()
        verify(view, times(1)).updateCoffeeProgress(0)
        verifyZeroInteractions(mockStatisticLogger)
    }

    @Test
    fun startDelayedCoffeeAnnouncement_WhenUpdaterAlreadyUpdating_ShowsCancelCoffeeProgressPrompt() {
        updater.isUpdating = true
        presenter.startDelayedCoffeeAnnouncement("")

        verify(view, times(1)).showCancelCoffeeProgressPrompt()
        verifyZeroInteractions(mockStatisticLogger)
    }

    @Test
    fun startDelayedCoffeeAnnouncement_WhenRunToEnd_UpdatesCoffeeProgressAndPostsToSlack() {
        mockServer.enqueue(MockResponse().setBody(""))
        presenter.startDelayedCoffeeAnnouncement("A happy message about coffee status")

        updater.run()
        updater.run()
        updater.run()

        val inOrder = inOrder(view, mockStatisticLogger)
        inOrder.verify(view).updateCoffeeProgress(0)
        inOrder.verify(view).updateCoffeeProgress(33)
        inOrder.verify(view).updateCoffeeProgress(67)
        inOrder.verify(view).resetCoffeeViewStatus()
        inOrder.verify(mockStatisticLogger).recordCoffeeBrewingEvent()

        val apiRequest = mockServer.takeRequest()
        assertThat(apiRequest.path, equalTo("/chat.postMessage"))

        val requestBody = apiRequest.body.readUtf8()
        assertThat(requestBody, containsString("token=${BuildConfig.SLACK_AUTH_TOKEN}"))
        assertThat(requestBody, containsString("channel=$CHANNEL_NAME"))
        assertThat(requestBody, containsString("text=A%20happy%20message%20about%20coffee%20status"))
        assertThat(requestBody, containsString("as_user=false"))
    }

    @Test
    fun cancelCoffeeCountDown_ResetsUpdaterAndUpdatesView() {
        presenter.cancelCoffeeCountDown()

        verify(view).updateCoffeeProgress(0)
        verify(view).resetCoffeeViewStatus()

        verify(mockHandler).removeCallbacks(updater)
        verifyZeroInteractions(mockStatisticLogger)
    }
}