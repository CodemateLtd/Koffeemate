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

import android.graphics.Bitmap
import com.codemate.koffeemate.common.AwardBadgeCreator
import com.codemate.koffeemate.data.local.CoffeeEventRepository
import com.codemate.koffeemate.data.local.CoffeePreferences
import com.codemate.koffeemate.data.models.User
import com.codemate.koffeemate.data.network.SlackApi
import com.codemate.koffeemate.extensions.toRequestBody
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.ResponseBody
import retrofit2.Response
import rx.Observable
import rx.Scheduler

open class PostAccidentUseCase(
        var slackApi: SlackApi,
        val coffeeEventRepository: CoffeeEventRepository,
        val coffeePreferences: CoffeePreferences,
        val awardBadgeCreator: AwardBadgeCreator,
        var subscriber: Scheduler,
        var observer: Scheduler
) {
    fun execute(
            comment: String,
            user: User,
            profilePic: Bitmap
    ): Observable<Response<ResponseBody>> {
        coffeeEventRepository.recordBrewingAccident(user)

        val awardCount = coffeeEventRepository.getAccidentCountForUser(user)
        val profilePicWithAward = awardBadgeCreator.createBitmapFileWithAward(profilePic, awardCount)

        // Evaluates to "johns-certificate.png" etc
        val fileName = "${user.profile.first_name.toLowerCase()}s-certificate.png"
        val channel = coffeePreferences.getAccidentChannel()

        return slackApi.postImage(
                MultipartBody.Part.createFormData(
                        "file",
                        fileName,
                        RequestBody.create(
                                MediaType.parse("image/png"),
                                profilePicWithAward
                        )
                ),
                fileName.toRequestBody(),
                channel.toRequestBody(),
                comment.toRequestBody()
        ).subscribeOn(subscriber).observeOn(observer)
    }
}