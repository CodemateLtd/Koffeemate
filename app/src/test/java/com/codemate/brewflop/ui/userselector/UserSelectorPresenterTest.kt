package com.codemate.brewflop.ui.userselector

import com.codemate.brewflop.BuildConfig
import com.codemate.brewflop.RegexMatcher.Companion.matchesPattern
import com.codemate.brewflop.SynchronousExecutorService
import com.codemate.brewflop.data.local.CoffeeStatisticLogger
import com.codemate.brewflop.data.network.SlackApi
import com.codemate.brewflop.data.network.SlackService
import com.codemate.brewflop.data.network.model.Profile
import com.codemate.brewflop.data.network.model.User
import okhttp3.Dispatcher
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.hamcrest.core.IsEqual.equalTo
import org.hamcrest.core.StringContains.containsString
import org.junit.After
import org.junit.Assert.assertThat
import org.junit.Before
import org.junit.Test
import org.mockito.Mockito.*
import java.io.File

class UserSelectorPresenterTest {
    private lateinit var mockCoffeeStatLogger: CoffeeStatisticLogger
    private lateinit var mockServer: MockWebServer
    private lateinit var slackApi: SlackApi
    private lateinit var presenter: UserSelectorPresenter
    private lateinit var view: UserSelectorView

    @Before
    fun setUp() {
        mockCoffeeStatLogger = mock(CoffeeStatisticLogger::class.java)

        mockServer = MockWebServer()
        mockServer.start()

        slackApi = SlackService.getApi(Dispatcher(SynchronousExecutorService()), mockServer.url("/"))
        presenter = UserSelectorPresenter(mockCoffeeStatLogger, slackApi)
        view = mock(UserSelectorView::class.java)

        presenter.attachView(view)
    }

    @After
    fun tearDown() {
        mockServer.shutdown()
    }

    @Test
    fun loadUsers_MakesRequestToRightPathWithToken() {
        mockServer.enqueue(MockResponse().setBody(""))
        presenter.loadUsers()

        val request = mockServer.takeRequest()
        assertThat(request.path, equalTo("/users.list?token=${BuildConfig.SLACK_AUTH_TOKEN}"))
    }

    @Test
    fun loadUsers_OnError_ShowsErrorOnUI() {
        mockServer.enqueue(MockResponse().setResponseCode(400))
        presenter.loadUsers()

        verify(view, times(1)).showProgress()
        verify(view, times(1)).showError()
        verifyNoMoreInteractions(view)
    }

    @Test
    fun loadUsers_OnInvalidResponseBody_ShowsErrorOnUI() {
        mockServer.enqueue(MockResponse().setBody("What is love?"))
        presenter.loadUsers()

        verify(view, times(1)).showProgress()
        verify(view, times(1)).showError()
        verifyNoMoreInteractions(view)
    }

    @Test
    fun announceCoffeeBrewingAccident_ShouldMakeCorrectRequest() {
        val user = getFakeUser()

        mockServer.enqueue(MockResponse().setBody(""))
        presenter.announceCoffeeBrewingAccident("test-channel", "Test comment", user, File.createTempFile("test", "png"))

        // TODO: There has to be a better way to verify these multipart post params, right? :S
        val requestBody = mockServer.takeRequest().body.readUtf8()
        assertThat(requestBody, containsString("filename=\"jormas-certificate.png\""))
        assertThat(requestBody, matchesPattern(".*channels.*test-channel.*"))
        assertThat(requestBody, matchesPattern(".*initial_comment.*Test comment.*"))
        assertThat(requestBody, matchesPattern(".*token.*${BuildConfig.SLACK_AUTH_TOKEN}.*"))
    }

    private fun getFakeUser(): User {
        val user = User()
        user.profile = Profile()
        user.profile.first_name = "Jorma"

        return user
    }
}