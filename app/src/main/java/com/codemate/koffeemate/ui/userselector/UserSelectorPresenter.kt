package com.codemate.koffeemate.ui.userselector

import android.graphics.Bitmap
import com.codemate.koffeemate.data.network.models.User
import com.codemate.koffeemate.ui.base.BasePresenter
import okhttp3.ResponseBody
import retrofit2.Response
import rx.Subscriber
import javax.inject.Inject

class UserSelectorPresenter @Inject constructor(
        val loadUsersUseCase: LoadUsersUseCase,
        val postAccidentUseCase: PostAccidentUseCase
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

    fun announceCoffeeBrewingAccident(comment: String, user: User, profilePic: Bitmap) {
        ensureViewIsAttached()

        postAccidentUseCase.execute(comment, user, profilePic).subscribe(
                object : Subscriber<Response<ResponseBody>>() {
                    override fun onNext(response: Response<ResponseBody>) {
                        getView()?.showAccidentPostedSuccessfullyMessage()
                    }

                    override fun onError(e: Throwable?) {
                        getView()?.showErrorPostingAccidentMessage()
                    }

                    override fun onCompleted() {
                    }
                })
    }
}