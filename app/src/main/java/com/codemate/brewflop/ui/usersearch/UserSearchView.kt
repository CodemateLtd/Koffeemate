package com.codemate.brewflop.ui.usersearch

import com.codemate.brewflop.data.network.model.User
import com.codemate.brewflop.ui.base.MvpView

interface UserSearchView : MvpView {
    fun showSearchResults(users: List<User>)
    fun showProgress()
    fun hideProgress()
    fun showError()
}