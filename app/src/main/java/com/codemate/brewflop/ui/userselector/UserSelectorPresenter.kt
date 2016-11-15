package com.codemate.brewflop.ui.userselector

import com.codemate.brewflop.BuildConfig
import com.codemate.brewflop.data.network.SlackApi
import com.codemate.brewflop.data.network.model.UserListResponse
import com.codemate.brewflop.ui.base.BasePresenter
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class UserSelectorPresenter(private val slackApi: SlackApi) : BasePresenter<UserSelectorView>() {
    fun loadUsers() {
        super.ensureViewIsAttached()
        getView()?.showProgress()

        slackApi.getUsers(BuildConfig.SLACK_AUTH_TOKEN).enqueue(object : Callback<UserListResponse> {
            override fun onResponse(call: Call<UserListResponse>, response: Response<UserListResponse>) {
                if (response.isSuccessful) {
                    val users = response.body().members
                            .toMutableList()
                            .filter { !it.isBot
                                        && !it.profile.firstName.startsWith("Ext-")
                                        && it.profile.realName != "slackbot"
                            }
                            .sortedBy { it.profile.realNameNormalized }

                    getView()?.showSearchResults(users)
                } else {
                    getView()?.showError()
                }
            }

            override fun onFailure(call: Call<UserListResponse>, t: Throwable) {
                getView()?.showError()
            }
        })

        getView()?.hideProgress()
    }
}