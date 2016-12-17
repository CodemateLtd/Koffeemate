package com.codemate.brewflop.ui.userselector

import com.codemate.brewflop.BuildConfig
import com.codemate.brewflop.data.local.CoffeeStatisticLogger
import com.codemate.brewflop.data.network.SlackApi
import com.codemate.brewflop.data.network.model.User
import com.codemate.brewflop.data.network.model.UserListResponse
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

class UserSelectorPresenter(
        private val coffeeStatisticLogger: CoffeeStatisticLogger,
        private val slackApi: SlackApi) : BasePresenter<UserSelectorView>() {
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
                            .sortedBy { it.profile.real_name_normalized }

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

    fun announceCoffeeBrewingAccident(channelName: String, comment: String, user: User, stickeredProfilePic: File) {
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
                channels = channelName.toRequestBody(),
                comment = comment.toRequestBody()
        ).enqueue(object : Callback<ResponseBody> {
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

        coffeeStatisticLogger.recordBrewingAccident(user)
    }
}