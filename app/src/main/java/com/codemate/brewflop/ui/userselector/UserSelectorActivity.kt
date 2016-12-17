package com.codemate.brewflop.ui.userselector

import android.graphics.Bitmap
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.view.MenuItem
import android.view.View
import com.bumptech.glide.Glide
import com.bumptech.glide.request.animation.GlideAnimation
import com.bumptech.glide.request.target.SimpleTarget
import com.codemate.brewflop.Constants
import com.codemate.brewflop.R
import com.codemate.brewflop.data.StickerApplier
import com.codemate.brewflop.data.local.RealmCoffeeEventRepository
import com.codemate.brewflop.data.network.SlackApi
import com.codemate.brewflop.data.network.SlackService
import com.codemate.brewflop.data.network.models.User
import kotlinx.android.synthetic.main.activity_user_selector.*
import kotlinx.android.synthetic.main.activity_user_selector.view.*
import org.jetbrains.anko.*

class UserSelectorActivity : AppCompatActivity(), UserSelectorView {
    private lateinit var userSelectorAdapter: UserSelectorAdapter
    private lateinit var stickerApplier: StickerApplier
    private lateinit var presenter: UserSelectorPresenter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_selector)
        setUpUserRecycler()

        stickerApplier = StickerApplier(this, R.drawable.approved_sticker)
        presenter = UserSelectorPresenter(
                RealmCoffeeEventRepository(),
                SlackService.getApi(SlackApi.BASE_URL)
        )
        presenter.attachView(this)
        presenter.loadUsers()

        errorLayout.tryAgain.onClick {
            presenter.loadUsers()
        }

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
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
                val comment = getString(R.string.congratulations_to_user_fmt, user.profile.first_name)

                Glide.with(this@UserSelectorActivity)
                        .load(user.profile.largestAvailableImage)
                        .asBitmap()
                        .into(object : SimpleTarget<Bitmap>(512, 512) {
                            override fun onResourceReady(resource: Bitmap, glideAnimation: GlideAnimation<in Bitmap>?) {
                                doAsync {
                                    val stickeredProfilePic = stickerApplier.applySticker(resource)

                                    uiThread {
                                        presenter.announceCoffeeBrewingAccident(
                                                Constants.ACCIDENT_ANNOUNCEMENT_CHANNEL,
                                                comment,
                                                user,
                                                stickeredProfilePic
                                        )
                                    }
                                }
                            }
                        })
            }
        }.show()
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