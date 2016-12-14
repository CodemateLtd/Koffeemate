package com.codemate.brewflop.ui.userselector

import com.codemate.brewflop.BuildConfig
import com.codemate.brewflop.data.local.BrewFailureLogger
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

class UserSelectorPresenter(
        private val slackApi: SlackApi,
        private val brewFailureLogger: BrewFailureLogger,
        private val stickerApplier: StickerApplier) : BasePresenter<UserSelectorView>() {
    fun loadUsers() {
        super.ensureViewIsAttached()
        getView()?.showProgress()

        slackApi.getUsers(BuildConfig.SLACK_AUTH_TOKEN).enqueue(object : Callback<UserListResponse> {
            override fun onResponse(call: Call<UserListResponse>, response: Response<UserListResponse>) {
                if (response.isSuccessful) {
                    val users = response.body().members
                            .toMutableList()
                            .filter {
                                !it.isBot
                                        && !it.profile.firstName.startsWith("Ext-")
                                        && it.realName != "slackbot"
                            }
                            .sortedBy { it.profile.realNameNormalized }

                    getView()?.showSearchResults(users)
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

    fun postMessageToSlack(user: User) {
        super.ensureViewIsAttached()

        stickerApplier.applySticker(user) { stickeredProfilePic ->
            val token = BuildConfig.SLACK_AUTH_TOKEN.toRequestBody()
            val channelName = "iiro-test".toRequestBody()
            val comment = "jorma".toRequestBody()
            val fileName = "${user.profile.firstName.toLowerCase()}s-moccamaster-certificate.png"
            val filePart = RequestBody.create(MediaType.parse("image/png"), stickeredProfilePic)
            val fileBody = MultipartBody.Part.createFormData("file", fileName, filePart)

            slackApi.postImage(
                    token = token,
                    channels = channelName,
                    comment = comment,
                    filename = fileName.toRequestBody(),
                    file = fileBody).enqueue(object : Callback<ResponseBody> {
                override fun onResponse(call: Call<ResponseBody>?, response: Response<ResponseBody>) {
                    if (response.isSuccessful) {
                        getView()?.messagePostedSuccessfully()
                        brewFailureLogger.incrementFailureCountForUser(user)
                    } else {
                        getView()?.errorPostingMessage()
                    }
                }

                override fun onFailure(call: Call<ResponseBody>?, t: Throwable?) {
                    getView()?.errorPostingMessage()
                }
            })
        }
    }
}