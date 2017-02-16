package com.codemate.koffeemate.ui.main

import android.graphics.Bitmap
import com.codemate.koffeemate.common.BrewingProgressUpdater
import com.codemate.koffeemate.common.ScreenSaver
import com.codemate.koffeemate.data.local.CoffeeEventRepository
import com.codemate.koffeemate.data.local.CoffeePreferences
import com.codemate.koffeemate.data.models.User
import com.codemate.koffeemate.ui.base.BasePresenter
import com.codemate.koffeemate.ui.userselector.UserSelectListener
import com.codemate.koffeemate.usecases.PostAccidentUseCase
import com.codemate.koffeemate.usecases.SendCoffeeAnnouncementUseCase
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
    var personBrewingCoffee: User? = null

    fun setScreenSaver(screensaver: ScreenSaver) {
        this.screensaver = screensaver
    }

    fun startDelayedCoffeeAnnouncement(newCoffeeMessage: String) {
        ensureViewIsAttached()
        screensaver?.defer()

        if (shouldAskForAnnouncementChannel()) {
            getView()?.showNoAnnouncementChannelSetError()
            return
        }

        if (!brewingProgressUpdater.isUpdating) {
            getView()?.showNewCoffeeIsComing()
            personBrewingCoffee = null

            if(!displayUserQuickDial()) {
                getView()?.displayUserSetterButton()
            }

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

    private fun shouldAskForAnnouncementChannel(): Boolean {
        return !brewingProgressUpdater.isUpdating
                && !coffeePreferences.isCoffeeAnnouncementChannelSet()
    }

    fun handlePersonChange() {
        if (personBrewingCoffee == null) {
            getView()?.hideUserSetterButton()

            if(!displayUserQuickDial()) {
                getView()?.displayFullscreenUserSelector(UserSelectListener.REQUEST_WHOS_BREWING)
            }
        } else {
            personBrewingCoffee = null
            getView()?.clearCoffeeBrewingPerson()
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
        personBrewingCoffee = null
    }

    fun launchAccidentReportingScreen() {
        screensaver?.defer()

        if (coffeePreferences.isAccidentChannelSet()) {
            personBrewingCoffee?.let {
                getView()?.showPostAccidentAnnouncementPrompt(it)
                return
            }

            getView()?.displayFullscreenUserSelector(UserSelectListener.REQUEST_WHO_FAILED_BREWING)
        } else {
            getView()?.showNoAccidentChannelSetError()
        }
    }

    fun announceCoffeeBrewingAccident(comment: String, user: User, profilePic: Bitmap) {
        ensureViewIsAttached()

        postAccidentUseCase.execute(comment, user, profilePic).subscribe(
                object : Subscriber<Response<ResponseBody>>() {
                    override fun onNext(response: Response<ResponseBody>) {
                        getView()?.showAccidentPostedSuccessfullyMessage()
                        personBrewingCoffee = null
                    }

                    override fun onError(e: Throwable?) {
                        getView()?.showErrorPostingAccidentMessage()
                    }

                    override fun onCompleted() {
                    }
                })
    }

    private fun displayUserQuickDial(): Boolean {
        val latestBrewers = coffeeEventRepository.getLatestBrewers().take(4)

        if (latestBrewers.isNotEmpty()) {
            getView()?.displayUserSelectorQuickDial(latestBrewers)
            return true
        }

        return false
    }
}