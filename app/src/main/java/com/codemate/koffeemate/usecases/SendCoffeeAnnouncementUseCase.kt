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

package com.codemate.koffeemate.usecases

import com.codemate.koffeemate.data.network.SlackApi
import okhttp3.ResponseBody
import retrofit2.Response
import rx.Observable
import rx.Scheduler
import javax.inject.Inject
import javax.inject.Named

open class SendCoffeeAnnouncementUseCase @Inject constructor(
        var slackApi: SlackApi,
        @Named("subscriber") var subscriber: Scheduler,
        @Named("observer") var observer: Scheduler
) {
    fun execute(channel: String, newCoffeeMessage: String): Observable<Response<ResponseBody>> {
        return slackApi.postMessage(channel, newCoffeeMessage)
                .subscribeOn(subscriber)
                .observeOn(observer)
    }
}