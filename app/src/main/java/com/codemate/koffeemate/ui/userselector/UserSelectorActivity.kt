package com.codemate.koffeemate.ui.userselector

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.view.MenuItem
import android.view.View
import com.bumptech.glide.Glide
import com.codemate.koffeemate.KoffeemateApp
import com.codemate.koffeemate.R
import com.codemate.koffeemate.data.network.models.User
import com.codemate.koffeemate.util.extensions.loadBitmap
import kotlinx.android.synthetic.main.activity_user_selector.*
import kotlinx.android.synthetic.main.activity_user_selector.view.*
import org.jetbrains.anko.alert
import org.jetbrains.anko.onClick
import org.jetbrains.anko.toast
import javax.inject.Inject

class UserSelectorActivity : AppCompatActivity(), UserSelectorView {
    private lateinit var userSelectorAdapter: UserSelectorAdapter

    @Inject
    lateinit var presenter: UserSelectorPresenter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_selector)
        KoffeemateApp.appComponent.inject(this)

        setUpUserRecycler()

        presenter.attachView(this)
        presenter.loadUsers()

        errorLayout.tryAgain.onClick {
            presenter.loadUsers()
        }

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    private fun setUpUserRecycler() {
        userSelectorAdapter = UserSelectorAdapter { user ->
            confirmUser(user)
        }

        userRecycler.layoutManager = LinearLayoutManager(this)
        userRecycler.adapter = userSelectorAdapter
    }

    private fun confirmUser(user: User) {
        alert {
            title(R.string.reset_the_counter)
            message(getString(R.string.posting_to_slack_fmt, user.profile.real_name))

            negativeButton(R.string.button_cancel)
            positiveButton(R.string.button_announce_coffee_accident) {
                applyStickerToProfilePicAndAnnounce(user)
            }
        }.show()
    }

    private fun applyStickerToProfilePicAndAnnounce(user: User) {
        val comment = getString(R.string.congratulations_to_user_fmt, user.profile.first_name)

        Glide.with(this).loadBitmap(user.profile.largestAvailableImage) { profilePic ->
            presenter.announceCoffeeBrewingAccident(comment, user, profilePic)
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            finish()
        }

        return super.onOptionsItemSelected(item)
    }

    override fun onDestroy() {
        super.onDestroy()
        presenter.detachView()
    }

    override fun showUsers(users: List<User>) {
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