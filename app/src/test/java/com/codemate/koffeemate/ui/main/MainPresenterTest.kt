package com.codemate.koffeemate.ui.main

import android.content.SharedPreferences
import android.graphics.Bitmap
import android.os.Handler
import com.codemate.koffeemate.common.AwardBadgeCreator
import com.codemate.koffeemate.common.BrewingProgressUpdater
import com.codemate.koffeemate.common.ScreenSaver
import com.codemate.koffeemate.data.local.CoffeeEventRepository
import com.codemate.koffeemate.data.local.CoffeePreferences
import com.codemate.koffeemate.data.local.models.CoffeeBrewingEvent
import com.codemate.koffeemate.data.network.SlackApi
import com.codemate.koffeemate.testutils.getResourceFile
import com.nhaarman.mockito_kotlin.*
import okhttp3.MediaType
import okhttp3.ResponseBody
import org.junit.Before
import org.junit.Test
import org.mockito.Mock
import org.mockito.MockitoAnnotations
import retrofit2.Response
import rx.Observable
import rx.schedulers.Schedulers

class MainPresenterTest {
    val CHANNEL_NAME = "fake-channel"

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
    lateinit var mockUpdater: BrewingProgressUpdater

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

        mockUpdater.updateHandler = mockHandler
        whenever(mockHandler.removeCallbacks(any())).then {
            // No-op
        }
        whenever(mockUpdater.reset()).then {
            // No-op
        }

        whenever(mockAwardBadgeCreator.createBitmapFileWithAward(any(), any()))
                .thenReturn(getResourceFile("images/empty.png"))

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
                mockUpdater,
                mockSendCoffeeAnnouncementUseCase,
                postAccidentUseCase
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
    fun startDelayedCoffeeAnnouncement_WhenChannelNameNotSet_AndIsNotUpdatingProgress_InformsView() {
        whenever(mockCoffeePreferences.getCoffeeAnnouncementChannel()).thenReturn("")
        presenter.startDelayedCoffeeAnnouncement("")

        verify(view, times(1)).showNoAnnouncementChannelSetError()
        verifyNoMoreInteractions(view)
        verifyZeroInteractions(mockCoffeeEventRepository)
    }

    @Test
    fun startDelayedCoffeeAnnouncement_WhenUpdaterNotUpdating_TellsViewNewCoffeeIsComing() {
        whenever(mockCoffeePreferences.isCoffeeAnnouncementChannelSet()).thenReturn(true)
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
    fun launchUserSelector_DefersScreenSaver() {
        presenter.launchAccidentReportingScreen()
        verify(mockScreenSaver).defer()
    }

    @Test
    fun launchUserSelector_WhenNoAccidentChannelSet_InformsView() {
        whenever(mockCoffeePreferences.getAccidentChannel()).thenReturn("")
        presenter.launchAccidentReportingScreen()

        verify(view).showNoAccidentChannelSetError()
        verifyNoMoreInteractions(view)
    }

    @Test
    fun launchUserSelector_WhenAccidentChannelSet_LaunchesUserSelector() {
        whenever(mockCoffeePreferences.isAccidentChannelSet()).thenReturn(true)
        presenter.launchAccidentReportingScreen()

        verify(view).launchUserSelector()
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
        presenter.cancelCoffeeCountDown()

        verify(view).updateCoffeeProgress(0)
        verify(view).resetCoffeeViewStatus()

        verify(mockHandler).removeCallbacks(mockUpdater)
        verifyZeroInteractions(mockCoffeeEventRepository)
    }

    @Test
    fun announceCoffeeBrewingAccident_OnSuccess_ShowsMessageOnUI() {
        whenever(mockSlackApi.postImage(any(), any(), any(), any(), any()))
                .thenReturn(Observable.just(Response.success(
                        ResponseBody.create(MediaType.parse("text/plain"), ""))
                ))

        presenter.announceCoffeeBrewingAccident("", "", "", mock<Bitmap>())

        verify(view).showAccidentPostedSuccessfullyMessage()
        verifyNoMoreInteractions(view)
    }

    @Test
    fun announceCoffeeBrewingAccident_OnError_ShowsErrorOnUI() {
        whenever(mockSlackApi.postImage(any(), any(), any(), any(), any()))
                .thenReturn(Observable.error(Throwable()))

        presenter.announceCoffeeBrewingAccident("", "", "", mock<Bitmap>())

        verify(view).showErrorPostingAccidentMessage()
    }
}