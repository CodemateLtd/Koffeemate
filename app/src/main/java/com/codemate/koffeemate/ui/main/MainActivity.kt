package com.codemate.koffeemate.ui.main

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.view.WindowManager
import com.codemate.koffeemate.KoffeemateApp
import com.codemate.koffeemate.R
import com.codemate.koffeemate.common.ScreenSaver
import com.codemate.koffeemate.data.local.models.CoffeeBrewingEvent
import com.codemate.koffeemate.di.modules.ActivityModule
import com.codemate.koffeemate.ui.settings.SettingsActivity
import com.codemate.koffeemate.ui.userselector.UserSelectorActivity
import kotlinx.android.synthetic.main.activity_main.*
import org.jetbrains.anko.*
import javax.inject.Inject

class MainActivity : AppCompatActivity(), MainView {
    @Inject
    lateinit var presenter: MainPresenter

    @Inject
    lateinit var screensaver: ScreenSaver

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

        settingsButton.onClick {
            screensaver.defer()
            startActivity(intentFor<SettingsActivity>())
        }

        logAccidentButton.onClick { presenter.launchUserSelector() }
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
    }

    override fun showNewCoffeeIsComing() {
        coffeeStatusTitle.text = getString(R.string.title_coffeeview_brewing)
        coffeeStatusMessage.text = getString(R.string.message_coffeeview_brewing)
        coffeeProgressView.setCoffeeIncoming()
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
    }

    override fun showNoAnnouncementChannelSetError() {
        longToast(R.string.prompt_no_announcement_channel_set)
        startActivity(intentFor<SettingsActivity>())
    }

    override fun showNoAccidentChannelSetError() {
        longToast(R.string.prompt_no_accident_channel_set)
        startActivity(intentFor<SettingsActivity>())
    }

    override fun launchAccidentReportingScreen() {
        startActivity(intentFor<UserSelectorActivity>())
    }
}