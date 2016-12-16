package com.codemate.brewflop.ui.userselector

import com.codemate.brewflop.SynchronousExecutorService
import com.codemate.brewflop.data.StickerApplier
import com.codemate.brewflop.data.local.CoffeeStatisticLogger
import com.codemate.brewflop.data.network.SlackApi
import com.codemate.brewflop.data.network.SlackService
import okhttp3.Dispatcher
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.mockito.Mockito
import org.mockito.Mockito.*

class UserSelectorPresenterTest {
    private lateinit var mockCoffeeStatLogger: CoffeeStatisticLogger
    private lateinit var mockStickerApplier: StickerApplier
    private lateinit var mockServer: MockWebServer
    private lateinit var slackApi: SlackApi
    private lateinit var presenter: UserSelectorPresenter
    private lateinit var view: UserSelectorView

    @Before
    fun setUp() {
        mockCoffeeStatLogger = mock(CoffeeStatisticLogger::class.java)
        mockStickerApplier = mock(StickerApplier::class.java)

        mockServer = MockWebServer()
        mockServer.start()

        slackApi = SlackService.getApi(Dispatcher(SynchronousExecutorService()), mockServer.url("/"))
        presenter = UserSelectorPresenter(mockCoffeeStatLogger, mockStickerApplier, slackApi)
        view = mock(UserSelectorView::class.java)

        presenter.attachView(view)
    }

    @After
    fun tearDown() {
        mockServer.shutdown()
    }

    @Test
    fun loadUsers_OnHttpError_ShowsErrorOnUI() {
        mockServer.enqueue(MockResponse().setResponseCode(400))
        presenter.loadUsers()

        verify(view, times(1)).showProgress()
        verify(view, Mockito.times(1)).showError()
        verifyNoMoreInteractions(view)
    }

    @Test
    fun testloadUsers_OnHttpOKButInvalidResponseBody_ShowsErrorOnUI() {
        mockServer.enqueue(MockResponse().setResponseCode(200).setBody("What is love?"))
        presenter.loadUsers()

        verify(view, times(1)).showProgress()
        verify(view, times(1)).showError()
        verifyNoMoreInteractions(view)
    }
}