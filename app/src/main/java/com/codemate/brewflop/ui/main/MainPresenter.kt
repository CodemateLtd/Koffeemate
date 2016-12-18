/*
 * Copyright 2016 Codemate Ltd
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.codemate.brewflop.ui.main

import com.codemate.brewflop.data.BrewingProgressUpdater
import com.codemate.brewflop.data.local.CoffeeEventRepository
import com.codemate.brewflop.data.local.CoffeePreferences
import com.codemate.brewflop.data.network.SlackApi
import com.codemate.brewflop.ui.base.BasePresenter
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
    fun startDelayedCoffeeAnnouncement(newCoffeeMessage: String) {
        ensureViewIsAttached()

        if (!brewingProgressUpdater.isUpdating
                && !coffeePreferences.isCoffeeAnnouncementChannelSet()) {
            getView()?.noAnnouncementChannelSet()
            return
        }

        if (!brewingProgressUpdater.isUpdating) {
            getView()?.newCoffeeIsComing()

            brewingProgressUpdater.startUpdating(
                    updateListener = { progress ->
                        getView()?.updateCoffeeProgress(progress)
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
            getView()?.setLastBrewingEvent(it)
        }
    }

    fun cancelCoffeeCountDown() {
        ensureViewIsAttached()

        getView()?.updateCoffeeProgress(0)
        getView()?.resetCoffeeViewStatus()

        brewingProgressUpdater.reset()
    }

    fun launchUserSelector() {
        if (!coffeePreferences.isAccidentChannelSet()) {
            getView()?.noAccidentChannelSet()
        } else {
            getView()?.launchUserSelector()
        }
    }
}