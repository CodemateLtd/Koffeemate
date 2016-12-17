package com.codemate.brewflop.ui.userselector

import com.codemate.brewflop.data.network.models.User
import com.codemate.brewflop.ui.base.MvpView

interface UserSelectorView : MvpView {
    fun showUsers(users: List<User>)
    fun showProgress()
    fun hideProgress()
    fun showError()
    fun messagePostedSuccessfully()
    fun errorPostingMessage()
}