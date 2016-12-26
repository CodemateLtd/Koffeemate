package com.codemate.koffeemate.ui.main

import com.codemate.koffeemate.common.BrewingProgressUpdater
import com.codemate.koffeemate.common.ScreenSaver
import com.codemate.koffeemate.data.local.CoffeeEventRepository
import com.codemate.koffeemate.data.local.CoffeePreferences
import com.codemate.koffeemate.data.network.SlackApi
import com.codemate.koffeemate.ui.base.BasePresenter
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import javax.inject.Inject

class MainPresenter @Inject constructor(
        val coffeePreferences: CoffeePreferences,
        val coffeeEventRepository: CoffeeEventRepository,
        val brewingProgressUpdater: BrewingProgressUpdater,
        val slackApi: SlackApi
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

            brewingProgressUpdater.startUpdating(
                    updateListener = { progress ->
                        // For UX: this way the user gets instant feedback, as the waves
                        // can't be below 10%
                        val adjustedProgress = Math.max(10, progress)

                        getView()?.updateCoffeeProgress(adjustedProgress)
                    },
                    completeListener = {
                        val channel = coffeePreferences.getCoffeeAnnouncementChannel()

                        slackApi.postMessage(
                                channel,
                                newCoffeeMessage
                        ).enqueue(object : Callback<ResponseBody>{
                            override fun onResponse(call: Call<ResponseBody>?, response: Response<ResponseBody>?) {
                                // TODO: Something. Do nothing for now.
                            }

                            override fun onFailure(call: Call<ResponseBody>?, t: Throwable?) {
                                t?.printStackTrace()
                            }
                        })

                        getView()?.updateCoffeeProgress(0)
                        getView()?.resetCoffeeViewStatus()

                        coffeeEventRepository.recordBrewingEvent()
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