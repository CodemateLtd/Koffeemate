package com.codemate.brewflop.ui.userselector

import com.codemate.brewflop.data.network.model.User
import com.codemate.brewflop.ui.base.MvpView

interface UserSelectorView : MvpView {
    fun showSearchResults(users: List<User>)
    fun showProgress()
    fun hideProgress()
    fun showError()
}