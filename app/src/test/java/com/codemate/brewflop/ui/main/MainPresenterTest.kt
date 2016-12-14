package com.codemate.brewflop.ui.main

import android.content.Context
import android.content.SharedPreferences
import android.os.Handler
import com.codemate.brewflop.BuildConfig
import com.codemate.brewflop.data.local.CoffeePreferences
import com.codemate.brewflop.data.network.SlackApi
import com.codemate.brewflop.data.network.SlackService
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.hamcrest.Matchers.containsString
import org.hamcrest.core.IsEqual.equalTo
import org.junit.After
import org.junit.Assert.assertThat
import org.junit.Before
import org.junit.Test
import org.mockito.Mockito
import org.mockito.Mockito.*

class MainPresenterTest {
    private lateinit var coffeePreferences: CoffeePreferences

    private lateinit var mockHandler: Handler
    private lateinit var updater: BrewingProgressUpdater

    private lateinit var slackApi: SlackApi

    private lateinit var presenter: MainPresenter
    private lateinit var view: MainView

    private lateinit var mockServer: MockWebServer

    @Before
    fun setUp() {
        coffeePreferences = mock(CoffeePreferences::class.java)
        coffeePreferences.preferences = mock(SharedPreferences::class.java)
        `when`(coffeePreferences.getChannelName()).thenReturn("fake-channel")

        mockHandler = mock(Handler::class.java)
        updater = BrewingProgressUpdater(9, 3)
        updater.updateHandler = mockHandler

        mockServer = MockWebServer()
        mockServer.start()

        slackApi = SlackService.getApi(mockServer.url("/"))
        presenter = MainPresenter(coffeePreferences, updater, slackApi)

        view = mock(MainView::class.java)
        presenter.attachView(view)
    }

    @After
    fun tearDown() {
        mockServer.shutdown()
    }

    @Test
    fun startCountDownForNewCoffee_WhenChannelNameNotSet_InformsView() {
        `when`(coffeePreferences.getChannelName()).thenReturn("")

        presenter.startCountDownForNewCoffee("")

        verify(view, times(1)).noChannelNameSet()
        verifyNoMoreInteractions(view)
    }

    @Test
    fun startCountDownForNewCoffee_WhenUpdaterNotUpdating_TellsViewNewCoffeeIsComing() {
        presenter.startCountDownForNewCoffee("")

        verify(view, times(1)).newCoffeeIsComing()
        verify(view, times(1)).updateCoffeeProgress(0)
    }

    @Test
    fun startCountDownForNewCoffee_WhenUpdaterAlreadyUpdating_ShowsCancelCoffeeProgressPrompt() {
        updater.isUpdating = true
        presenter.startCountDownForNewCoffee("")

        verify(view, times(1)).showCancelCoffeeProgressPrompt()
    }

    @Test
    fun startCountDownForNewCoffee_WhenRunToEnd_UpdatesCoffeeProgressAndPostsToSlack() {
        mockServer.enqueue(MockResponse().setBody(""))
        presenter.startCountDownForNewCoffee("A happy message about coffee status")

        updater.run()
        updater.run()
        updater.run()

        val inOrder = inOrder(view)
        inOrder.verify(view).updateCoffeeProgress(0)
        inOrder.verify(view).updateCoffeeProgress(33)
        inOrder.verify(view).updateCoffeeProgress(67)
        inOrder.verify(view).updateCoffeeProgress(0)
        inOrder.verify(view).noCoffeeAnyMore()

        val apiRequest = mockServer.takeRequest()
        assertThat(apiRequest.path, equalTo("/chat.postMessage"))

        val requestBody = apiRequest.body.readUtf8()
        assertThat(requestBody, containsString("token=${BuildConfig.SLACK_AUTH_TOKEN}"))
        assertThat(requestBody, containsString("channel=${coffeePreferences.getChannelName()}"))
        assertThat(requestBody, containsString("text=A%20happy%20message%20about%20coffee%20status"))
        assertThat(requestBody, containsString("as_user=false"))
    }

    @Test
    fun cancelCoffeeCountDown_ResetsUpdaterAndUpdatesView() {
        presenter.cancelCoffeeCountDown()

        verify(view).updateCoffeeProgress(0)
        verify(view).noCoffeeAnyMore()

        verify(mockHandler).removeCallbacks(updater)
    }
}