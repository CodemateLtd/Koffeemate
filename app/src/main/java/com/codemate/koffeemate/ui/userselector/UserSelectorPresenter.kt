package com.codemate.koffeemate.ui.userselector

import com.codemate.koffeemate.ui.base.BasePresenter
import com.codemate.koffeemate.usecases.LoadUsersUseCase
import javax.inject.Inject

class UserSelectorPresenter @Inject constructor(
        val loadUsersUseCase: LoadUsersUseCase
) : BasePresenter<UserSelectorView>() {
    fun loadUsers() {
        ensureViewIsAttached()
        getView()?.showProgress()

        loadUsersUseCase.execute()
                .subscribe(
                        { users ->
                            getView()?.showUsers(users)
                            getView()?.hideProgress()
                        },
                        { getView()?.showError() }
                )
    }
}