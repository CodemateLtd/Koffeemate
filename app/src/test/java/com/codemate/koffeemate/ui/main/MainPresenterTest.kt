package com.codemate.koffeemate.ui.main

import android.content.SharedPreferences
import android.os.Handler
import com.codemate.koffeemate.BuildConfig
import com.codemate.koffeemate.testutils.SynchronousExecutorService
import com.codemate.koffeemate.data.BrewingProgressUpdater
import com.codemate.koffeemate.data.local.CoffeeEventRepository
import com.codemate.koffeemate.data.local.CoffeePreferences
import com.codemate.koffeemate.data.local.models.CoffeeBrewingEvent
import com.codemate.koffeemate.data.network.SlackApi
import com.codemate.koffeemate.data.network.SlackService
import com.nhaarman.mockito_kotlin.*
import okhttp3.Dispatcher
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.hamcrest.CoreMatchers.containsString
import org.hamcrest.core.IsEqual.equalTo
import org.junit.After
import org.junit.Assert.assertThat
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

    lateinit var updater: BrewingProgressUpdater
    lateinit var mockServer: MockWebServer
    lateinit var slackApi: SlackApi
    lateinit var presenter: MainPresenter

    @Before
    fun setUp() {
        MockitoAnnotations.initMocks(this)

        coffeePreferences.preferences = mock<SharedPreferences>()
        whenever(coffeePreferences.getAccidentChannel()).thenReturn(CHANNEL_NAME)
        whenever(coffeePreferences.getCoffeeAnnouncementChannel()).thenReturn(CHANNEL_NAME)

        updater = BrewingProgressUpdater(9, 3)
        updater.updateHandler = mockHandler

        mockServer = MockWebServer()
        mockServer.start()

        slackApi = SlackService.getApi(Dispatcher(SynchronousExecutorService()), mockServer.url("/"))

        presenter = MainPresenter(coffeePreferences, mockCoffeeEventRepository, updater, slackApi)
        presenter.attachView(view)
    }

    @After
    fun tearDown() {
        mockServer.shutdown()
    }

    @Test
    fun launchUserSelector_WhenNoAccidentChannelSet_InformsView() {
        whenever(coffeePreferences.getAccidentChannel()).thenReturn("")
        presenter.launchUserSelector()

        verify(view).noAccidentChannelSet()
        verifyNoMoreInteractions(view)
    }

    @Test
    fun testName() {
        presenter.launchUserSelector()

        verify(view).launchUserSelector()
        verifyNoMoreInteractions(view)
    }

    @Test
    fun startDelayedCoffeeAnnouncement_WhenChannelNameNotSet_AndIsNotUpdatingProgress_InformsView() {
        whenever(coffeePreferences.getCoffeeAnnouncementChannel()).thenReturn("")
        updater.isUpdating = false

        presenter.startDelayedCoffeeAnnouncement("")

        verify(view, times(1)).noAnnouncementChannelSet()
        verifyNoMoreInteractions(view)
        verifyZeroInteractions(mockCoffeeEventRepository)
    }

    @Test
    fun startDelayedCoffeeAnnouncement_WhenUpdaterNotUpdating_TellsViewNewCoffeeIsComing() {
        presenter.startDelayedCoffeeAnnouncement("")

        verify(view).newCoffeeIsComing()
        verify(view).updateCoffeeProgress(10)

        verifyNoMoreInteractions(view)
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
            verify(view).updateCoffeeProgress(10)
            verify(view).updateCoffeeProgress(33)
            verify(view).updateCoffeeProgress(67)
            verify(view).updateCoffeeProgress(0)
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
    fun updateLastBrewingEventTime_WhenHasCoffeeBrewingEvents_ShowsLastInUI() {
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