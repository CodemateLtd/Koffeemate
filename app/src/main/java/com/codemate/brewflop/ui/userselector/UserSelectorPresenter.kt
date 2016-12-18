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

package com.codemate.brewflop.ui.userselector

import com.codemate.brewflop.BuildConfig
import com.codemate.brewflop.data.local.CoffeeEventRepository
import com.codemate.brewflop.data.local.CoffeePreferences
import com.codemate.brewflop.data.network.SlackApi
import com.codemate.brewflop.data.network.models.User
import com.codemate.brewflop.data.network.models.UserListResponse
import com.codemate.brewflop.ui.base.BasePresenter
import com.codemate.brewflop.util.extensions.toRequestBody
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File
import javax.inject.Inject

class UserSelectorPresenter @Inject constructor(
        val coffeePreferences: CoffeePreferences,
        val coffeeEventRepository: CoffeeEventRepository,
        val slackApi: SlackApi) : BasePresenter<UserSelectorView>() {
    fun loadUsers() {
        ensureViewIsAttached()
        getView()?.showProgress()

        slackApi.getUsers(BuildConfig.SLACK_AUTH_TOKEN).enqueue(object : Callback<UserListResponse> {
            override fun onResponse(call: Call<UserListResponse>, response: Response<UserListResponse>) {
                if (response.isSuccessful) {
                    val users = response.body().members
                            .toMutableList()
                            .filter {
                                !it.is_bot
                                        && !it.profile.first_name.startsWith("Ext-")
                                        && it.real_name != "slackbot"
                            }
                            .sortedBy { it.profile.real_name }

                    getView()?.showUsers(users)
                    getView()?.hideProgress()
                } else {
                    getView()?.showError()
                }
            }

            override fun onFailure(call: Call<UserListResponse>, t: Throwable) {
                getView()?.showError()
            }
        })
    }

    fun announceCoffeeBrewingAccident(comment: String, user: User, stickeredProfilePic: File) {
        ensureViewIsAttached()

        // Evaluates to "johns-certificate.png" etc
        val fileName = "${user.profile.first_name.toLowerCase()}s-certificate.png"
        val fileBody = MultipartBody.Part.createFormData(
                "file",
                fileName,
                RequestBody.create(
                        MediaType.parse("image/png"),
                        stickeredProfilePic
                )
        )

        slackApi.postImage(
                file = fileBody,
                filename = fileName.toRequestBody(),
                channels = coffeePreferences.getAccidentChannel().toRequestBody(),
                comment = comment.toRequestBody()).enqueue(object : Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>?, response: Response<ResponseBody>) {
                if (response.isSuccessful) {
                    getView()?.messagePostedSuccessfully()
                } else {
                    getView()?.errorPostingMessage()
                }
            }

            override fun onFailure(call: Call<ResponseBody>?, t: Throwable?) {
                getView()?.errorPostingMessage()
            }
        })

        coffeeEventRepository.recordBrewingAccident(user.id)
    }
}