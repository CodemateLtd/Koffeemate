package com.codemate.brewflop.ui.usersearch

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import com.codemate.brewflop.R
import com.codemate.brewflop.data.network.SlackApi
import com.codemate.brewflop.data.network.SlackService
import com.codemate.brewflop.data.network.model.User
import kotlinx.android.synthetic.main.activity_user_selector.*
import org.jetbrains.anko.toast

class UserSelectorActivity : AppCompatActivity(), UserSelectorView {
    private lateinit var userSelectorAdapter: UserSelectorAdapter
    private lateinit var presenter: UserSelectorPresenter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_selector)
        setUpUserRecycler()

        val searchTerm = intent.getStringExtra("search_term")

        presenter = UserSelectorPresenter(SlackService.getApi(SlackApi.BASE_URL))
        presenter.attachView(this)
        presenter.loadUsers(searchTerm)
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
    }

    override fun hideProgress() {
    }

    override fun showError() {
        toast("Something went wrong. :(")
    }
}
