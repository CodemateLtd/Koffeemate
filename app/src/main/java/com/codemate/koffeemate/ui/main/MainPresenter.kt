package com.codemate.koffeemate.ui.main

import com.codemate.koffeemate.common.BrewingProgressUpdater
import com.codemate.koffeemate.common.ScreenSaver
import com.codemate.koffeemate.data.local.CoffeeEventRepository
import com.codemate.koffeemate.data.local.CoffeePreferences
import com.codemate.koffeemate.ui.base.BasePresenter
import javax.inject.Inject

class MainPresenter @Inject constructor(
        val coffeePreferences: CoffeePreferences,
        val coffeeEventRepository: CoffeeEventRepository,
        val brewingProgressUpdater: BrewingProgressUpdater,
        val sendCoffeeAnnouncementUseCase: SendCoffeeAnnouncementUseCase
) : BasePresenter<MainView>() {
    private var screensaver: ScreenSaver? = null

    fun startDelayedCoffeeAnnouncement(newCoffeeMessage: String) {
        ensureViewIsAttached()
        screensaver?.defer()

        if (!brewingProgressUpdater.isUpdating
                && !coffeePreferences.isCoffeeAnnouncementChannelSet()) {
            getView()?.showNoAnnouncementChannelSetError()
            return
        }

        if (!brewingProgressUpdater.isUpdating) {
            getView()?.showNewCoffeeIsComing()

            sendCoffeeAnnouncementUseCase.execute(
                    newCoffeeMessage,
                    updateListener = { getView()?.updateCoffeeProgress(it) },
                    completeListener = {
                        getView()?.updateCoffeeProgress(0)
                        getView()?.resetCoffeeViewStatus()

                        updateLastBrewingEventTime()
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

    fun launchUserSelector() {
        screensaver?.defer()

        if (!coffeePreferences.isAccidentChannelSet()) {
            getView()?.showNoAccidentChannelSetError()
        } else {
            getView()?.launchAccidentReportingScreen()
        }
    }

    fun setScreenSaver(screensaver: ScreenSaver) {
        this.screensaver = screensaver
    }
}