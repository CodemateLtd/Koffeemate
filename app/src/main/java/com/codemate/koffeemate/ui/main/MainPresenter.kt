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
import rx.Subscription
import javax.inject.Inject

class MainPresenter @Inject constructor(
        val coffeePreferences: CoffeePreferences,
        val coffeeEventRepository: CoffeeEventRepository,
        val brewingProgressUpdater: BrewingProgressUpdater,
        val sendCoffeeAnnouncementUseCase: SendCoffeeAnnouncementUseCase,
        val postAccidentUseCase: PostAccidentUseCase
) : BasePresenter<MainView>() {
    private var screensaver: ScreenSaver? = null

    var progressSubscription: Subscription? = null
    var personBrewingCoffee: User? = null
    var isUpdating: Boolean = false

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

        if (!isUpdating) {
            getView()?.showNewCoffeeIsComing()

            isUpdating = true
            personBrewingCoffee = null

            if (!displayUserQuickDial()) {
                getView()?.displayUserSetterButton()
            }

            progressSubscription = brewingProgressUpdater
                    .start(coffeePreferences.getCoffeeBrewingTime())
                    .map { progress ->
                        // For UX: this way the user gets instant feedback, as the waves
                        // can't be below 10%
                        Math.max(10, progress)
                    }
                    .doOnNext { progress -> getView()?.updateCoffeeProgress(progress) }
                    .doOnCompleted {
                        sendCoffeeAnnouncementUseCase
                                .execute(coffeePreferences.getCoffeeAnnouncementChannel(), newCoffeeMessage)
                                .subscribe(
                                        {
                                            coffeeEventRepository.recordBrewingEvent(personBrewingCoffee)
                                            updateLastBrewingEventTime()

                                            cancelCoffeeCountDown()
                                        },
                                        { e -> e.printStackTrace() }
                                )

                        isUpdating = false
                    }.subscribe()
        } else {
            getView()?.showCancelCoffeeProgressPrompt()
        }
    }

    private fun shouldAskForAnnouncementChannel(): Boolean {
        return !isUpdating && !coffeePreferences.isCoffeeAnnouncementChannelSet()
    }

    fun handlePersonChange() {
        if (personBrewingCoffee == null) {
            getView()?.hideUserSetterButton()

            if (!displayUserQuickDial()) {
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

        progressSubscription?.unsubscribe()
        progressSubscription = null
        isUpdating = false
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

        postAccidentUseCase
                .execute(comment, user, profilePic)
                .subscribe(
                        {
                            getView()?.showAccidentPostedSuccessfullyMessage()
                            personBrewingCoffee = null
                        },
                        { e ->
                            e.printStackTrace()
                            getView()?.showErrorPostingAccidentMessage()
                        }
                )
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