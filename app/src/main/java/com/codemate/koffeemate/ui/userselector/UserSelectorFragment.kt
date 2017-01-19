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

package com.codemate.koffeemate.ui.userselector

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.support.annotation.Nullable
import android.support.v4.app.DialogFragment
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.codemate.koffeemate.KoffeemateApp
import com.codemate.koffeemate.R
import com.codemate.koffeemate.common.BasicListItemAnimator
import com.codemate.koffeemate.data.network.models.User
import kotlinx.android.synthetic.main.fragment_user_selector.*
import kotlinx.android.synthetic.main.fragment_user_selector.view.*
import org.jetbrains.anko.onClick
import javax.inject.Inject

class UserSelectorFragment : DialogFragment(), UserSelectorView {
    private lateinit var userSelectorAdapter: UserSelectorAdapter
    private lateinit var userSelectListener: UserSelectListener

    @Inject
    lateinit var presenter: UserSelectorPresenter

    companion object {
        fun newInstance(): UserSelectorFragment {
            val fragment = UserSelectorFragment()
            fragment.setStyle(DialogFragment.STYLE_NORMAL, R.style.TitledDialog)

            return fragment
        }
    }

    interface UserSelectListener {
        fun onUserSelected(user: User)
    }

    @Suppress("UNCHECKED_CAST")
    override fun onAttach(context: Context) {
        super.onAttach(context)

        try {
            userSelectListener = context as UserSelectListener
        } catch (e: ClassCastException) {
            throw ClassCastException("You must implement UserSelectListener " +
                    "in your calling Activity before attaching UserSelectorFragment instances.")
        }
    }

    override fun onCreateView(inflater: LayoutInflater?, @Nullable container: ViewGroup?, @Nullable savedInstanceState: Bundle?): View? {
        return inflater?.inflate(R.layout.fragment_user_selector, container, false)
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        KoffeemateApp.appComponent.inject(this)
        setUpUserRecycler()

        presenter.attachView(this)
        presenter.loadUsers()

        errorLayout.tryAgain.onClick {
            presenter.loadUsers()
        }
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = super.onCreateDialog(savedInstanceState)
        dialog.setTitle(R.string.prompt_select_person_below)

        return dialog
    }

    private fun setUpUserRecycler() {
        userSelectorAdapter = UserSelectorAdapter { user ->
            userSelectListener.onUserSelected(user)
            dismiss()
        }

        userRecycler.adapter = userSelectorAdapter
        userRecycler.layoutManager = LinearLayoutManager(context)
        userRecycler.itemAnimator = BasicListItemAnimator(context)
    }

    override fun onDestroy() {
        super.onDestroy()
        presenter.detachView()
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