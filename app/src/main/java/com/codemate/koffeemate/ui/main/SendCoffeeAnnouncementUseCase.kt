/*
 * Copyright 2017 Codemate Ltd
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

package com.codemate.koffeemate.ui.main

import com.codemate.koffeemate.common.BrewingProgressUpdater
import com.codemate.koffeemate.data.local.CoffeeEventRepository
import com.codemate.koffeemate.data.local.CoffeePreferences
import com.codemate.koffeemate.data.network.SlackApi
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import javax.inject.Inject

open class SendCoffeeAnnouncementUseCase @Inject constructor(
        val brewingProgressUpdater: BrewingProgressUpdater?,
        val coffeePreferences: CoffeePreferences,
        val slackApi: SlackApi,
        val coffeeEventRepository: CoffeeEventRepository
) {
    fun execute(newCoffeeMessage: String, updateListener: (Int) -> Unit, completeListener: () -> Unit) {
        brewingProgressUpdater?.startUpdating(
                updateListener = { progress ->
                    // For UX: this way the user gets instant feedback, as the waves
                    // can't be below 10%
                    val adjustedProgress = Math.max(10, progress)
                    updateListener(adjustedProgress)
                },
                completeListener = {
                    val channel = coffeePreferences.getCoffeeAnnouncementChannel()

                    slackApi.postMessage(channel, newCoffeeMessage)
                            .enqueue(object : Callback<ResponseBody> {
                                override fun onResponse(call: Call<ResponseBody>?, response: Response<ResponseBody>?) {
                                    // TODO: Something. Do nothing for now.
                                }

                                override fun onFailure(call: Call<ResponseBody>?, t: Throwable?) {
                                    t?.printStackTrace()
                                }
                            })

                    coffeeEventRepository.recordBrewingEvent()
                    completeListener()
                }
        )
    }
}