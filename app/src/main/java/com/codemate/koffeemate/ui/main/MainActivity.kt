package com.codemate.koffeemate.ui.main

import android.app.ProgressDialog
import android.os.Bundle
import android.support.v4.view.ViewCompat
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.view.WindowManager
import com.bumptech.glide.Glide
import com.codemate.koffeemate.KoffeemateApp
import com.codemate.koffeemate.R
import com.codemate.koffeemate.common.ScreenSaver
import com.codemate.koffeemate.data.models.CoffeeBrewingEvent
import com.codemate.koffeemate.data.models.User
import com.codemate.koffeemate.di.modules.ActivityModule
import com.codemate.koffeemate.extensions.loadBitmap
import com.codemate.koffeemate.ui.settings.SettingsActivity
import com.codemate.koffeemate.ui.userselector.UserSelectorOverlay
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.view_coffee_progress.view.*
import org.jetbrains.anko.*
import javax.inject.Inject

class MainActivity : AppCompatActivity(), MainView, UserSelectorOverlay.UserSelectListener {
    private val REQUEST_WHOS_BREWING = 1
    private val REQUEST_WHO_FAILED_BREWING = 2

    @Inject
    lateinit var presenter: MainPresenter

    @Inject
    lateinit var screensaver: ScreenSaver

    var accidentProgress: ProgressDialog? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        KoffeemateApp.appComponent
                .plus(ActivityModule(this))
                .inject(this)

        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_FULLSCREEN

        presenter.attachView(this)
        presenter.setScreenSaver(screensaver)

        setUpListeners()
    }

    fun setUpListeners() {
        coffeeProgressView.setOnCoffeePotClickListener {
            val newCoffeeMessage = getString(R.string.message_new_coffee_available)
            presenter.startDelayedCoffeeAnnouncement(newCoffeeMessage)
        }

        coffeeProgressView.setOnUserSetterClickListener {
            presenter.handlePersonChange()
        }

        settingsButton.onClick {
            screensaver.defer()
            startActivity(intentFor<SettingsActivity>())
        }

        logAccidentButton.onClick { presenter.launchAccidentReportingScreen() }
    }

    override fun onStart() {
        super.onStart()
        screensaver.start()
    }

    override fun onResume() {
        super.onResume()
        presenter.updateLastBrewingEventTime()
    }

    override fun onStop() {
        super.onStop()
        screensaver.stop()
    }

    override fun onDestroy() {
        super.onDestroy()
        presenter.detachView()
        accidentProgress?.dismiss()
    }

    private fun showUserSelector(requestCode: Int) {
        val selector = UserSelectorOverlay(this)
        selector.userSelectListener = this
        selector.requestCode = requestCode

        ViewCompat.setElevation(selector, dip(12).toFloat())
        container.addView(selector)
    }

    // Functions for identifying who brews the coffee
    override fun selectCoffeeBrewingPerson() {
        showUserSelector(REQUEST_WHOS_BREWING)
    }

    override fun clearCoffeeBrewingPerson() {
        coffeeProgressView.userSetterButton.clearUser()
    }

    override fun onUserSelected(user: User, requestCode: Int) {
        when (requestCode) {
            REQUEST_WHOS_BREWING -> {
                Glide.with(this)
                        .load(user.profile.smallestAvailableImage)
                        .error(R.drawable.ic_user_unknown)
                        .into(coffeeProgressView.userSetterButton)
                presenter.personBrewingCoffee = user
            }
            REQUEST_WHO_FAILED_BREWING -> {
                showPostAccidentAnnouncementPrompt(user)
            }
        }
    }

    // MainView methods -->
    override fun showNewCoffeeIsComing() {
        coffeeStatusTitle.text = getString(R.string.title_coffeeview_brewing)
        coffeeStatusMessage.text = getString(R.string.message_coffeeview_brewing)
        coffeeProgressView.setCoffeeIncoming()

        logAccidentButton.hide()
    }

    override fun showCancelCoffeeProgressPrompt() {
        alert {
            title(R.string.prompt_cancel_coffee_progress_title)
            message(R.string.prompt_cancel_coffee_progress_message)

            negativeButton(R.string.action_no)
            positiveButton(R.string.action_yes) {
                presenter.cancelCoffeeCountDown()
            }
        }.show()
    }

    override fun updateLastBrewingEvent(event: CoffeeBrewingEvent) {
        lastBrewingEventTime.setTime(event.time)
    }

    override fun updateCoffeeProgress(newProgress: Int) {
        coffeeProgressView.setProgress(newProgress)
    }

    override fun resetCoffeeViewStatus() {
        coffeeStatusTitle.text = getString(R.string.title_coffeeview_idle)
        coffeeStatusMessage.text = getString(R.string.message_coffeeview_idle)
        coffeeProgressView.reset()

        logAccidentButton.show()
    }

    override fun showNoAnnouncementChannelSetError() {
        longToast(R.string.prompt_no_announcement_channel_set)
        startActivity(intentFor<SettingsActivity>())
    }

    override fun showNoAccidentChannelSetError() {
        longToast(R.string.prompt_no_accident_channel_set)
        startActivity(intentFor<SettingsActivity>())
    }

    // Shaming users for coffee brewing failures -->
    override fun launchUserSelector() {
        showUserSelector(REQUEST_WHO_FAILED_BREWING)
    }

    override fun showPostAccidentAnnouncementPrompt(user: User) {
        alert {
            title(R.string.prompt_reset_the_counter)
            message(getString(R.string.message_posting_to_slack_fmt, user.profile.real_name))

            negativeButton(R.string.action_cancel)
            positiveButton(R.string.action_announce_coffee_accident) {
                accidentProgress = indeterminateProgressDialog(R.string.progress_message_shaming_person_on_slack)
                val comment = getString(R.string.message_congratulations_to_user_fmt, user.profile.first_name)

                Glide.with(this@MainActivity)
                        .loadBitmap(user.profile.largestAvailableImage) { profilePic ->
                    presenter.announceCoffeeBrewingAccident(comment, user, profilePic)
                }
            }
        }.show()
    }

    override fun showAccidentPostedSuccessfullyMessage() {
        accidentProgress?.dismiss()
        toast(R.string.message_posted_successfully)
    }

    override fun showErrorPostingAccidentMessage() {
        accidentProgress?.dismiss()
        toast(R.string.error_could_not_post_message)
    }
}