package com.codemate.brewflop.ui.usersearch

import com.codemate.brewflop.BuildConfig
import com.codemate.brewflop.data.network.SlackApi
import com.codemate.brewflop.data.network.model.User
import com.codemate.brewflop.data.network.model.UserListResponse
import com.codemate.brewflop.ui.base.BasePresenter
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class UserSelectorPresenter(private val slackApi: SlackApi) : BasePresenter<UserSelectorView>() {
    fun loadUsers(searchTerm: String) {
        super.ensureViewIsAttached()
        getView()?.showProgress()

        slackApi.getUsers(BuildConfig.SLACK_AUTH_TOKEN).enqueue(object : Callback<UserListResponse> {
            override fun onResponse(call: Call<UserListResponse>, response: Response<UserListResponse>) {
                if (response.isSuccessful) {
                    val users = response.body().members
                            .toMutableList()
                            .filter { !it.isBot && search(it, searchTerm) }
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

    private fun search(user: User, searchTerm: String): Boolean {
        val firstNameNormalized = normalize(user.profile.firstName)
        val searchTermNormalized = normalize(searchTerm)

        return firstNameNormalized.contains(searchTermNormalized)
    }

    // Takes in a freak and makes it normal.
    private fun normalize(freak: String): String {
        return freak.trim().toLowerCase()
    }
}