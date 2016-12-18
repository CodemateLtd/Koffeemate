package com.codemate.brewflop.ui.userselector

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.view.MenuItem
import android.view.View
import com.bumptech.glide.Glide
import com.codemate.brewflop.BrewFlopApp
import com.codemate.brewflop.BuildConfig
import com.codemate.brewflop.R
import com.codemate.brewflop.data.StickerApplier
import com.codemate.brewflop.data.network.models.User
import com.codemate.brewflop.util.extensions.loadBitmap
import kotlinx.android.synthetic.main.activity_user_selector.*
import kotlinx.android.synthetic.main.activity_user_selector.view.*
import org.jetbrains.anko.*
import javax.inject.Inject

class UserSelectorActivity : AppCompatActivity(), UserSelectorView {
    private lateinit var userSelectorAdapter: UserSelectorAdapter
    private lateinit var stickerApplier: StickerApplier

    @Inject
    lateinit var presenter: UserSelectorPresenter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_selector)
        BrewFlopApp.appComponent.inject(this)

        setUpUserRecycler()
        stickerApplier = StickerApplier(this, R.drawable.approved_sticker)

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

        Glide.with(this).loadBitmap(user.profile.largestAvailableImage) {
            doAsync {
                val stickeredProfilePic = stickerApplier.applySticker(it)

                uiThread {
                    presenter.announceCoffeeBrewingAccident(
                            BuildConfig.BREWING_ACCIDENT_CHANNEL,
                            comment,
                            user,
                            stickeredProfilePic
                    )
                }
            }
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