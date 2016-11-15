package com.codemate.brewflop.ui.userselector

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.view.View
import com.codemate.brewflop.DayCountUpdater
import com.codemate.brewflop.R
import com.codemate.brewflop.data.network.SlackApi
import com.codemate.brewflop.data.network.SlackService
import com.codemate.brewflop.data.network.SlackWebHookApi
import com.codemate.brewflop.data.network.model.User
import kotlinx.android.synthetic.main.activity_user_selector.*
import kotlinx.android.synthetic.main.activity_user_selector.view.*
import org.jetbrains.anko.alert
import org.jetbrains.anko.onClick
import org.jetbrains.anko.toast

class UserSelectorActivity : AppCompatActivity(), UserSelectorView {
    private lateinit var userSelectorAdapter: UserSelectorAdapter
    private lateinit var presenter: UserSelectorPresenter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_selector)
        setUpUserRecycler()

        presenter = UserSelectorPresenter(
                SlackService.getApi(SlackApi.BASE_URL),
                SlackService.getWebhookApi(SlackWebHookApi.BASE_URL),
                DayCountUpdater(this)
        )
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
        userSelectorAdapter = UserSelectorAdapter { user ->
            confirmUser(user)
        }

        userRecycler.layoutManager = LinearLayoutManager(this)
        userRecycler.adapter = userSelectorAdapter
    }

    private fun confirmUser(user: User) {
        val title = getString(R.string.reset_the_counter)
        val message = getString(R.string.posting_to_slack_fmt, user.profile.realName)

        alert(message, title) {
            negativeButton(R.string.try_again)
            neutralButton(R.string.cancel)
            positiveButton(R.string.inform_everyone) {
                presenter.postMessageToSlack(
                        user,
                        getString(R.string.slack_announcement_fmt)
                )
            }
        }.show()
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

    override fun messagePostedSuccessfully() {
        toast(R.string.message_posted_successfully)
        finish()
    }

    override fun errorPostingMessage() {
        toast(R.string.could_not_post_message)
    }

}
