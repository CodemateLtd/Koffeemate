package com.codemate.koffeemate.ui.userselector

import android.content.SharedPreferences
import android.graphics.Bitmap
import com.codemate.koffeemate.BuildConfig
import com.codemate.koffeemate.testutils.RegexMatcher.Companion.matchesPattern
import com.codemate.koffeemate.testutils.SynchronousExecutorService
import com.codemate.koffeemate.common.AwardBadgeCreator
import com.codemate.koffeemate.data.local.CoffeeEventRepository
import com.codemate.koffeemate.data.local.CoffeePreferences
import com.codemate.koffeemate.data.network.SlackApi
import com.codemate.koffeemate.data.network.SlackService
import com.codemate.koffeemate.data.network.models.Profile
import com.codemate.koffeemate.data.network.models.User
import com.nhaarman.mockito_kotlin.*
import okhttp3.Dispatcher
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.hamcrest.core.IsEqual.equalTo
import org.hamcrest.core.StringContains.containsString
import org.junit.After
import org.junit.Assert.assertThat
import org.junit.Before
import org.junit.Test
import org.mockito.Mock
import org.mockito.MockitoAnnotations
import java.io.File

class UserSelectorPresenterTest {
    val TEST_USER_ID = "abc123"

    @Mock
    lateinit var mockCoffeePreferences: CoffeePreferences

    @Mock
    lateinit var mockCoffeeEventRepository: CoffeeEventRepository

    @Mock
    lateinit var mockAwardBadgeCreator: AwardBadgeCreator

    @Mock
    lateinit var mockBitmap: Bitmap

    @Mock
    lateinit var view: UserSelectorView

    lateinit var mockServer: MockWebServer
    lateinit var slackApi: SlackApi
    lateinit var presenter: UserSelectorPresenter

    @Before
    fun setUp() {
        MockitoAnnotations.initMocks(this)

        mockCoffeePreferences.preferences = mock<SharedPreferences>()
        whenever(mockCoffeePreferences.getAccidentChannel()).thenReturn("test-channel")
        whenever(mockCoffeeEventRepository.getAccidentCountForUser(TEST_USER_ID)).thenReturn(1)

        whenever(mockAwardBadgeCreator.createBitmapFileWithAward(mockBitmap, 1))
                .thenReturn(File("src/test/resources/images/empty.png"))

        mockServer = MockWebServer()
        mockServer.start()

        slackApi = SlackService.getApi(Dispatcher(SynchronousExecutorService()), mockServer.url("/"))
        presenter = UserSelectorPresenter(
                mockCoffeePreferences,
                mockCoffeeEventRepository,
                mockAwardBadgeCreator,
                slackApi
        )

        presenter.attachView(view)
    }

    @After
    fun tearDown() {
        mockServer.shutdown()
    }

    @Test
    fun loadUsers_LoadsUsersAndDisplaysThemInUI() {
        val userListJson = File("src/test/resources/seeds/sample_userlist_response.json").readText()
        mockServer.enqueue(MockResponse().setBody(userListJson))
        presenter.loadUsers()

        argumentCaptor<List<User>>().apply {
            verify(view).showUsers(capture())

            val userList = firstValue
            assertThat(userList.size, equalTo(2))

            val bobby = userList[0]
            assertThat(bobby.id, equalTo("abc123"))
            assertThat(bobby.name, equalTo("bobby"))
            assertThat(bobby.is_bot, equalTo(false))
            assertThat(bobby.profile.first_name, equalTo("Bobby"))
            assertThat(bobby.profile.last_name, equalTo("Tables"))
            assertThat(bobby.profile.real_name, equalTo("Bobby Tables"))

            val john = userList[1]
            assertThat(john.id, equalTo("123abc"))
            assertThat(john.name, equalTo("john"))
            assertThat(john.is_bot, equalTo(false))
            assertThat(john.profile.first_name, equalTo("John"))
            assertThat(john.profile.last_name, equalTo("Smith"))
            assertThat(john.profile.real_name, equalTo("John Smith"))
        }

        verify(view).showProgress()
        verify(view).hideProgress()
        verifyNoMoreInteractions(view)
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

        verify(view).showProgress()
        verify(view).showError()
        verifyNoMoreInteractions(view)
    }

    @Test
    fun loadUsers_OnInvalidResponseBody_ShowsErrorOnUI() {
        mockServer.enqueue(MockResponse().setBody("What is love?"))
        presenter.loadUsers()

        verify(view).showProgress()
        verify(view).showError()
        verifyNoMoreInteractions(view)
    }

    @Test
    fun announceCoffeeBrewingAccident_ShouldMakeCorrectRequest() {
        val user = getFakeUser()

        mockServer.enqueue(MockResponse().setBody(""))
        presenter.announceCoffeeBrewingAccident("Test comment", user, mockBitmap)

        // TODO: There has to be a better way to verify these multipart post params, right? :S
        val requestBody = mockServer.takeRequest().body.readUtf8()
        assertThat(requestBody, containsString("filename=\"jormas-certificate.png\""))
        assertThat(requestBody, matchesPattern(".*channels.*test-channel.*"))
        assertThat(requestBody, matchesPattern(".*initial_comment.*Test comment.*"))
        assertThat(requestBody, matchesPattern(".*token.*${BuildConfig.SLACK_AUTH_TOKEN}.*"))
    }

    @Test
    fun announceCoffeeBrewingAccident_WhenSuccessful_NotifiesUIAndStoresEvent() {
        val user = getFakeUser()

        mockServer.enqueue(MockResponse().setBody(""))
        presenter.announceCoffeeBrewingAccident("", user, mockBitmap)

        verify(view).showAccidentPostedSuccessfullyMessage()
        verify(mockCoffeeEventRepository).recordBrewingAccident(user.id)
        verify(mockCoffeeEventRepository).getAccidentCountForUser(TEST_USER_ID)
        verifyNoMoreInteractions(view, mockCoffeeEventRepository)
    }

    @Test
    fun announceCoffeeBrewingAccident_WhenNotSuccessful_DisplaysErrorMessage() {
        val user = getFakeUser()

        mockServer.enqueue(MockResponse().setResponseCode(400))
        presenter.announceCoffeeBrewingAccident("", user, mockBitmap)

        verify(view).showErrorMessage()
        verify(mockCoffeeEventRepository).recordBrewingAccident(user.id)
        verify(mockCoffeeEventRepository).getAccidentCountForUser(TEST_USER_ID)
        verifyNoMoreInteractions(view, mockCoffeeEventRepository)
    }

    private fun getFakeUser(): User {
        val user = User()
        user.id = TEST_USER_ID
        user.profile = Profile()
        user.profile.first_name = "Jorma"

        return user
    }
}