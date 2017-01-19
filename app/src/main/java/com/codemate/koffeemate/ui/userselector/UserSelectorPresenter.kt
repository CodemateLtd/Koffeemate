package com.codemate.koffeemate.ui.userselector

import com.codemate.koffeemate.data.network.models.User
import com.codemate.koffeemate.ui.base.BasePresenter
import rx.Subscriber
import javax.inject.Inject

class UserSelectorPresenter @Inject constructor(
        val loadUsersUseCase: LoadUsersUseCase
) : BasePresenter<UserSelectorView>() {
    fun loadUsers() {
        ensureViewIsAttached()
        getView()?.showProgress()

        loadUsersUseCase.execute().subscribe(
                object : Subscriber<List<User>>() {
                    override fun onCompleted() {
                        getView()?.hideProgress()
                    }

                    override fun onNext(users: List<User>) {
                        getView()?.showUsers(users)
                    }

                    override fun onError(e: Throwable?) {
                        getView()?.showError()
                    }
                })
    }
}