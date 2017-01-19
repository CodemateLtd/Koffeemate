package com.codemate.koffeemate.ui.main

import android.graphics.Bitmap
import com.codemate.koffeemate.common.BrewingProgressUpdater
import com.codemate.koffeemate.common.ScreenSaver
import com.codemate.koffeemate.data.local.CoffeeEventRepository
import com.codemate.koffeemate.data.local.CoffeePreferences
import com.codemate.koffeemate.ui.base.BasePresenter
import okhttp3.ResponseBody
import retrofit2.Response
import rx.Subscriber
import javax.inject.Inject

class MainPresenter @Inject constructor(
        val coffeePreferences: CoffeePreferences,
        val coffeeEventRepository: CoffeeEventRepository,
        val brewingProgressUpdater: BrewingProgressUpdater,
        val sendCoffeeAnnouncementUseCase: SendCoffeeAnnouncementUseCase,
        val postAccidentUseCase: PostAccidentUseCase
) : BasePresenter<MainView>() {
    private var screensaver: ScreenSaver? = null
    private var personBrewingCoffee: String? = null

    fun setScreenSaver(screensaver: ScreenSaver) {
        this.screensaver = screensaver
    }

    fun setPersonBrewingCoffee(userId: String?) {
        this.personBrewingCoffee = userId
    }

    fun startDelayedCoffeeAnnouncement(newCoffeeMessage: String) {
        ensureViewIsAttached()
        screensaver?.defer()

        if (!brewingProgressUpdater.isUpdating
                && !coffeePreferences.isCoffeeAnnouncementChannelSet()) {
            getView()?.showNoAnnouncementChannelSetError()
            return
        }

        if (!brewingProgressUpdater.isUpdating) {
            personBrewingCoffee = null
            getView()?.showNewCoffeeIsComing()

            brewingProgressUpdater.startUpdating(
                    updateListener = { progress ->
                        // For UX: this way the user gets instant feedback, as the waves
                        // can't be below 10%
                        val adjustedProgress = Math.max(10, progress)
                        getView()?.updateCoffeeProgress(adjustedProgress)
                    },
                    completeListener = {
                        sendCoffeeAnnouncementUseCase
                                .execute(coffeePreferences.getCoffeeAnnouncementChannel(), newCoffeeMessage)
                                .subscribe(object : Subscriber<Response<ResponseBody>>() {
                                    override fun onError(e: Throwable?) {
                                        e?.printStackTrace()
                                    }

                                    override fun onCompleted() {
                                        getView()?.updateCoffeeProgress(0)
                                        getView()?.resetCoffeeViewStatus()

                                        coffeeEventRepository.recordBrewingEvent(personBrewingCoffee)
                                        updateLastBrewingEventTime()
                                    }

                                    override fun onNext(t: Response<ResponseBody>?) {
                                    }
                                })
                    }
            )
        } else {
            getView()?.showCancelCoffeeProgressPrompt()
        }
    }

    fun updateLastBrewingEventTime() {
        coffeeEventRepository.getLastBrewingEvent()?.let {
            getView()?.updateLastBrewingEvent(it)
        }
    }

    fun cancelCoffeeCountDown() {
        ensureViewIsAttached()

        getView()?.updateCoffeeProgress(0)
        getView()?.resetCoffeeViewStatus()

        brewingProgressUpdater.reset()
    }

    fun launchAccidentReportingScreen() {
        screensaver?.defer()

        if (coffeePreferences.isAccidentChannelSet()) {
            getView()?.launchUserSelector()
        } else {
            getView()?.showNoAccidentChannelSetError()
        }
    }

    fun announceCoffeeBrewingAccident(comment: String, userId: String, userName: String, profilePic: Bitmap) {
        ensureViewIsAttached()

        postAccidentUseCase.execute(comment, userId, userName, profilePic).subscribe(
                object : Subscriber<Response<ResponseBody>>() {
                    override fun onNext(response: Response<ResponseBody>) {
                        getView()?.showAccidentPostedSuccessfullyMessage()
                    }

                    override fun onError(e: Throwable?) {
                        getView()?.showErrorPostingAccidentMessage()
                    }

                    override fun onCompleted() {
                    }
                })
    }
}