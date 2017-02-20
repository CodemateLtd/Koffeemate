/*
 * Copyright 2017 Codemate Ltd
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.codemate.koffeemate.ui.userselector.views

import android.content.Context
import android.support.v7.widget.GridLayoutManager
import android.util.AttributeSet
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import com.codemate.koffeemate.KoffeemateApp
import com.codemate.koffeemate.R
import com.codemate.koffeemate.data.models.User
import com.codemate.koffeemate.ui.userselector.UserItemAnimator
import com.codemate.koffeemate.ui.userselector.UserSelectListener
import com.codemate.koffeemate.ui.userselector.UserSelectorPresenter
import com.codemate.koffeemate.ui.userselector.UserSelectorView
import com.codemate.koffeemate.ui.userselector.adapter.UserSelectorAdapter
import kotlinx.android.synthetic.main.view_user_selector.view.*
import org.jetbrains.anko.onClick
import javax.inject.Inject

class UserSelectorOverlay : FrameLayout, UserSelectorView {
    private lateinit var userSelectorAdapter: UserSelectorAdapter

    var requestCode: Int = 0
    var userSelectListener: UserSelectListener? = null

    @Inject
    lateinit var presenter: UserSelectorPresenter

    constructor(ctx: Context) : super(ctx)
    constructor(ctx: Context, attrs: AttributeSet) : super(ctx, attrs)

    init {
        inflate(context, R.layout.view_user_selector, this)
        alpha = 0f
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        KoffeemateApp.appComponent.inject(this)

        setUpUserRecycler()

        presenter.attachView(this)
        presenter.loadUsers()

        errorLayout.tryAgain.onClick {
            presenter.loadUsers()
        }

        animate().alpha(1f).start()
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        presenter.detachView()
    }

    private fun setUpUserRecycler() {
        userSelectorAdapter = UserSelectorAdapter { user ->
            userSelectListener?.onUserSelected(user, requestCode)
            (parent as ViewGroup).removeView(this)
        }

        userRecycler.adapter = userSelectorAdapter
        userRecycler.layoutManager = GridLayoutManager(context, 4)
        userRecycler.itemAnimator = UserItemAnimator()
    }

    override fun showProgress() {
        progress.visibility = View.VISIBLE
        errorLayout.visibility = View.GONE
    }

    override fun hideProgress() {
        progress.visibility = View.GONE
        errorLayout.visibility = View.GONE
    }

    override fun showError() {
        progress.visibility = View.GONE
        errorLayout.visibility = View.VISIBLE
    }

    override fun showUsers(users: List<User>) {
        userSelectorAdapter.setItems(users)
    }
}