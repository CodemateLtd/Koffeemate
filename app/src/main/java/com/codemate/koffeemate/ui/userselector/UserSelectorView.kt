package com.codemate.koffeemate.ui.userselector

import com.codemate.koffeemate.data.network.models.User
import com.codemate.koffeemate.ui.base.MvpView

interface UserSelectorView : MvpView {
    fun showProgress()
    fun hideProgress()
    fun showError()
    fun showUsers(users: List<User>)

    fun showAccidentPostedSuccessfullyMessage()
    fun showErrorMessage()
}