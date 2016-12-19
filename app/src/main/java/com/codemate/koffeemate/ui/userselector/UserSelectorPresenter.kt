package com.codemate.koffeemate.ui.userselector

import com.codemate.koffeemate.BuildConfig
import com.codemate.koffeemate.data.local.CoffeeEventRepository
import com.codemate.koffeemate.data.local.CoffeePreferences
import com.codemate.koffeemate.data.network.SlackApi
import com.codemate.koffeemate.data.network.models.User
import com.codemate.koffeemate.data.network.models.UserListResponse
import com.codemate.koffeemate.ui.base.BasePresenter
import com.codemate.koffeemate.util.extensions.toRequestBody
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