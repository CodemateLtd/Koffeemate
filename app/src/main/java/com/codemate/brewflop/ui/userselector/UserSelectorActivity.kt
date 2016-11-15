package com.codemate.brewflop.ui.userselector

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.view.View
import com.codemate.brewflop.R
import com.codemate.brewflop.data.network.SlackApi
import com.codemate.brewflop.data.network.SlackService
import com.codemate.brewflop.data.network.model.User
import kotlinx.android.synthetic.main.activity_user_selector.*
import kotlinx.android.synthetic.main.activity_user_selector.view.*
import org.jetbrains.anko.onClick

class UserSelectorActivity : AppCompatActivity(), UserSelectorView {
    private lateinit var userSelectorAdapter: UserSelectorAdapter
    private lateinit var presenter: UserSelectorPresenter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_selector)
        setUpUserRecycler()

        presenter = UserSelectorPresenter(SlackService.getApi(SlackApi.BASE_URL))
        presenter.attachView(this)
        presenter.loadUsers()

        errorLayout.tryAgain.onClick {
            presenter.loadUsers()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        presenter.detachView()
    }

    private fun setUpUserRecycler() {
        userSelectorAdapter = UserSelectorAdapter()

        userRecycler.layoutManager = LinearLayoutManager(this)
        userRecycler.adapter = userSelectorAdapter
    }

    override fun showSearchResults(users: List<User>) {
        userSelectorAdapter.setItems(users)
    }

    override fun showProgress() {
        progress.visibility = View.VISIBLE
        userRecycler.visibility = View.GONE
        errorLayout.visibility = View.GONE
    }

    override fun hideProgress() {
        progress.visibility = View.GONE
        userRecycler.visibility = View.VISIBLE
        errorLayout.visibility = View.GONE
    }

    override fun showError() {
        progress.visibility = View.GONE
        userRecycler.visibility = View.GONE
        errorLayout.visibility = View.VISIBLE
    }
}
