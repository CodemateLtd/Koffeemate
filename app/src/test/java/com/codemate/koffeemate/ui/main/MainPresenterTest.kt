package com.codemate.koffeemate.ui.main

import android.content.SharedPreferences
import android.graphics.Bitmap
import android.os.Handler
import com.codemate.koffeemate.common.AwardBadgeCreator
import com.codemate.koffeemate.common.BrewingProgressUpdater
import com.codemate.koffeemate.common.ScreenSaver
import com.codemate.koffeemate.data.local.CoffeeEventRepository
import com.codemate.koffeemate.data.local.CoffeePreferences
import com.codemate.koffeemate.data.models.CoffeeBrewingEvent
import com.codemate.koffeemate.data.models.User
import com.codemate.koffeemate.data.network.SlackApi
import com.codemate.koffeemate.testutils.fakeUser
import com.codemate.koffeemate.testutils.getResourceFile
import com.codemate.koffeemate.usecases.PostAccidentUseCase
import com.codemate.koffeemate.usecases.SendCoffeeAnnouncementUseCase
import com.nhaarman.mockito_kotlin.*
import okhttp3.MediaType
import okhttp3.ResponseBody
import org.hamcrest.core.IsEqual.equalTo
import org.hamcrest.core.IsNull.nullValue
import org.junit.Assert.assertThat
import org.junit.Before
import org.junit.Test
import org.mockito.Mock
import org.mockito.MockitoAnnotations
import retrofit2.Response
import rx.Observable
import rx.schedulers.Schedulers

class MainPresenterTest {
    val CHANNEL_NAME = "fake-channel"
    val emptySuccessResponse: Observable<Response<ResponseBody>> =
            Observable.just(
                    Response.success(
                            ResponseBody.create(
                                    MediaType.parse("text/plain"), "")
                    )
            )

    @Mock
    lateinit var mockCoffeePreferences: CoffeePreferences

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
    lateinit var updater: BrewingProgressUpdater

    @Mock
    lateinit var mockSlackApi: SlackApi

    @Mock
    lateinit var mockAwardBadgeCreator: AwardBadgeCreator

    lateinit var presenter: MainPresenter

    @Before
    fun setUp() {
        MockitoAnnotations.initMocks(this)

        mockCoffeePreferences.preferences = mock<SharedPreferences>()
        whenever(mockCoffeePreferences.getAccidentChannel()).thenReturn(CHANNEL_NAME)
        whenever(mockCoffeePreferences.getCoffeeAnnouncementChannel()).thenReturn(CHANNEL_NAME)

        whenever(mockHandler.removeCallbacks(any())).then {
            // No-op
        }

        updater = BrewingProgressUpdater(9, 3)
        updater.updateHandler = mockHandler

        whenever(mockAwardBadgeCreator.createBitmapFileWithAward(any(), any()))
                .thenReturn(getResourceFile("images/empty.png"))

        val sendCoffeeAnnouncementUseCase = SendCoffeeAnnouncementUseCase(
                mockSlackApi,
                Schedulers.immediate(),
                Schedulers.immediate()
        )

        val postAccidentUseCase = PostAccidentUseCase(
                mockSlackApi,
                mock<CoffeeEventRepository>(),
                mockCoffeePreferences,
                mockAwardBadgeCreator,
                Schedulers.immediate(),
                Schedulers.immediate()
        )

        presenter = MainPresenter(
                mockCoffeePreferences,
                mockCoffeeEventRepository,
                updater,
                sendCoffeeAnnouncementUseCase,
                postAccidentUseCase
        )
        presenter.attachView(view)
        presenter.setScreenSaver(mockScreenSaver)
    }

    @Test
    fun startDelayedCoffeeAnnouncement_HappyPathRunsToEnd() {
        whenever(mockCoffeePreferences.isCoffeeAnnouncementChannelSet()).thenReturn(true)
        whenever(mockSlackApi.postMessage(any(), any(), any(), any(), any(), any()))
                .thenReturn(emptySuccessResponse)

        val user = fakeUser()
        presenter.startDelayedCoffeeAnnouncement("")
        presenter.personBrewingCoffee = user

        updater.run()
        updater.run()
        updater.run()

        inOrder(view, mockCoffeeEventRepository) {
            verify(view).showNewCoffeeIsComing()
            verify(view).updateCoffeeProgress(10)
            verify(view).updateCoffeeProgress(33)
            verify(view).updateCoffeeProgress(67)
        }

        verify(view).updateCoffeeProgress(0)
        verify(view).resetCoffeeViewStatus()
        verify(mockCoffeeEventRepository).recordBrewingEvent(user)
    }

    @Test
    fun startDelayedCoffeeAnnouncement_ClearsPreviousPersonBrewingCoffee() {
        whenever(mockCoffeePreferences.isCoffeeAnnouncementChannelSet()).thenReturn(true)
        whenever(mockSlackApi.postMessage(any(), any(), any(), any(), any(), any()))
                .thenReturn(emptySuccessResponse)

        presenter.personBrewingCoffee = fakeUser()
        presenter.startDelayedCoffeeAnnouncement("")

        updater.run()
        updater.run()
        updater.run()

        verify(mockCoffeeEventRepository).recordBrewingEvent(null)
    }

    @Test
    fun startDelayedCoffeeAnnouncement_DefersScreenSaver() {
        presenter.startDelayedCoffeeAnnouncement("")
        verify(mockScreenSaver).defer()
    }

    @Test
    fun startDelayedCoffeeAnnouncement_WhenChannelNameNotSet_AndIsNotUpdatingProgress_InformsView() {
        whenever(mockCoffeePreferences.getCoffeeAnnouncementChannel()).thenReturn("")
        presenter.startDelayedCoffeeAnnouncement("")

        verify(view, times(1)).showNoAnnouncementChannelSetError()
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
    fun displayUserSelector_WhenNoTopBrewers_ShowsUserSetterButton() {
        whenever(mockCoffeeEventRepository.getTopBrewers()).thenReturn(emptyList())
        whenever(mockCoffeePreferences.isCoffeeAnnouncementChannelSet()).thenReturn(true)
        whenever(mockSlackApi.postMessage(any(), any(), any(), any(), any(), any()))
                .thenReturn(emptySuccessResponse)

        presenter.startDelayedCoffeeAnnouncement("")
        verify(view, times(1)).displayUserSetterButton()
    }

    @Test
    fun displayUserSelector_WhenBrewersExist_ShowsUserQuickDial() {
        val latestBrewers = listOf(fakeUser())

        whenever(mockCoffeeEventRepository.getLatestBrewers()).thenReturn(latestBrewers)
        whenever(mockCoffeePreferences.isCoffeeAnnouncementChannelSet()).thenReturn(true)
        whenever(mockSlackApi.postMessage(any(), any(), any(), any(), any(), any()))
                .thenReturn(emptySuccessResponse)

        presenter.startDelayedCoffeeAnnouncement("")
        verify(view, times(1)).displayUserSelectorQuickDial(latestBrewers)
    }

    @Test
    fun displayUserSelectorQuickDial_DisplaysFourLatesBrewersAtMost() {
        val latestBrewers = listOf(fakeUser(), fakeUser(), fakeUser(), fakeUser(), fakeUser())

        whenever(mockCoffeeEventRepository.getLatestBrewers()).thenReturn(latestBrewers)
        whenever(mockCoffeePreferences.isCoffeeAnnouncementChannelSet()).thenReturn(true)
        whenever(mockSlackApi.postMessage(any(), any(), any(), any(), any(), any()))
                .thenReturn(emptySuccessResponse)

        presenter.startDelayedCoffeeAnnouncement("")

        argumentCaptor<List<User>>().apply {
            verify(view, times(1)).displayUserSelectorQuickDial(capture())

            val users = allValues.first()
            assertThat(users.size, equalTo(4))
        }
    }

    @Test
    fun handlePersonChange_WhenPersonNotSet_ShowsPersonSelectionDialog() {
        presenter.handlePersonChange()

        verify(view).selectCoffeeBrewingPerson()
        verifyNoMoreInteractions(view)
    }

    @Test
    fun handlePersonChange_WhenPersonIsSet_ClearsPerson() {
        presenter.personBrewingCoffee = fakeUser()
        presenter.handlePersonChange()

        assertThat(presenter.personBrewingCoffee, nullValue())

        verify(view).clearCoffeeBrewingPerson()
        verifyNoMoreInteractions(view)
    }

    @Test
    fun launchAccidentReportingScreen_DefersScreenSaver() {
        presenter.launchAccidentReportingScreen()
        verify(mockScreenSaver).defer()
    }

    @Test
    fun launchAccidentReportingScreen_WhenNoAccidentChannelSet_InformsView() {
        whenever(mockCoffeePreferences.getAccidentChannel()).thenReturn("")
        presenter.launchAccidentReportingScreen()

        verify(view).showNoAccidentChannelSetError()
        verifyNoMoreInteractions(view)
    }

    @Test
    fun launchAccidentReportingScreen_WhenPersonBrewingCoffeeNotKnown_ShowsUserSelector() {
        whenever(mockCoffeePreferences.isAccidentChannelSet()).thenReturn(true)
        presenter.launchAccidentReportingScreen()

        verify(view).launchUserSelector()
        verifyNoMoreInteractions(view)
    }

    @Test
    fun launchAccidentReportingScreen_WhenPersonBrewingCoffeeIsKnown_SkipsUserSelector() {
        whenever(mockCoffeePreferences.isAccidentChannelSet()).thenReturn(true)

        val user = fakeUser()
        presenter.personBrewingCoffee = user
        presenter.launchAccidentReportingScreen()

        verify(view).showPostAccidentAnnouncementPrompt(user)
        verifyNoMoreInteractions(view)
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
        presenter.startDelayedCoffeeAnnouncement("")
        presenter.personBrewingCoffee = fakeUser()
        presenter.cancelCoffeeCountDown()

        verify(view).updateCoffeeProgress(0)
        verify(view).resetCoffeeViewStatus()
        verify(mockHandler).removeCallbacks(updater)
        verifyZeroInteractions(mockCoffeeEventRepository)

        assertThat(presenter.personBrewingCoffee, nullValue())
    }

    @Test
    fun announceCoffeeBrewingAccident_OnSuccess_ClearCoffeeBrewingPsrson() {
        whenever(mockSlackApi.postImage(any(), any(), any(), any(), any()))
                .thenReturn(emptySuccessResponse)

        presenter.personBrewingCoffee = fakeUser()
        presenter.announceCoffeeBrewingAccident("", fakeUser(), mock<Bitmap>())

        assertThat(presenter.personBrewingCoffee, nullValue())
    }

    @Test
    fun announceCoffeeBrewingAccident_OnSuccess_ShowsMessageOnUI() {
        whenever(mockSlackApi.postImage(any(), any(), any(), any(), any()))
                .thenReturn(emptySuccessResponse)

        presenter.announceCoffeeBrewingAccident("", fakeUser(), mock<Bitmap>())

        verify(view).showAccidentPostedSuccessfullyMessage()
        verifyNoMoreInteractions(view)
    }

    @Test
    fun announceCoffeeBrewingAccident_OnError_ShowsErrorOnUI() {
        whenever(mockSlackApi.postImage(any(), any(), any(), any(), any()))
                .thenReturn(Observable.error(Throwable()))

        presenter.announceCoffeeBrewingAccident("", fakeUser(), mock<Bitmap>())

        verify(view).showErrorPostingAccidentMessage()
    }
}