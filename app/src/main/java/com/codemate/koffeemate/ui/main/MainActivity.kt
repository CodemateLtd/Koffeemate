package com.codemate.koffeemate.ui.main

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.view.WindowManager
import com.codemate.koffeemate.KoffeemateApp
import com.codemate.koffeemate.R
import com.codemate.koffeemate.data.ScreenSaverManager
import com.codemate.koffeemate.data.local.models.CoffeeBrewingEvent
import com.codemate.koffeemate.ui.secretsettings.SecretSettingsActivity
import com.codemate.koffeemate.ui.userselector.UserSelectorActivity
import kotlinx.android.synthetic.main.activity_main.*
import org.jetbrains.anko.*
import javax.inject.Inject

class MainActivity : AppCompatActivity(), MainView {
    @Inject
    lateinit var presenter: MainPresenter
    lateinit var screenSaverManager: ScreenSaverManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        screenSaverManager = ScreenSaverManager.attach(this)
        KoffeemateApp.appComponent.inject(this)

        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_FULLSCREEN

        presenter.attachView(this)
        setUpListeners()
    }

    fun setUpListeners() {
        coffeeProgressView.onClick {
            screenSaverManager.deferScreenSaver()

            val newCoffeeMessage = getString(R.string.new_coffee_available)
            presenter.startDelayedCoffeeAnnouncement(newCoffeeMessage)
        }

        coffeeProgressView.onLongClick {
            screenSaverManager.deferScreenSaver()

            startActivity(intentFor<SecretSettingsActivity>())
            true
        }

        logAccidentButton.onClick { presenter.launchUserSelector() }
    }

    override fun onResume() {
        super.onResume()
        presenter.updateLastBrewingEventTime()
    }

    override fun onDestroy() {
        super.onDestroy()
        presenter.detachView()
    }

    override fun noAnnouncementChannelSet() {
        longToast(R.string.no_announcement_channel_set)
        startActivity(intentFor<SecretSettingsActivity>())
    }

    override fun noAccidentChannelSet() {
        longToast(R.string.no_accident_channel_set)
        startActivity(intentFor<SecretSettingsActivity>())
    }

    override fun launchUserSelector() {
        startActivity(intentFor<UserSelectorActivity>())
    }

    override fun setLastBrewingEvent(event: CoffeeBrewingEvent) {
        lastBrewingEventTime.setTime(event.time)
    }

    override fun newCoffeeIsComing() {
        coffeeStatusTitle.text = getString(R.string.title_coffeeview_brewing)
        coffeeStatusMessage.text = getString(R.string.message_coffeeview_brewing)
        coffeeProgressView.animate()
                .alpha(1f)
                .start()
    }

    override fun updateCoffeeProgress(newProgress: Int) {
        // For UX: this way the user gets instant feedback, as the waves
        // can't be below 10%
        val adjustedProgress = if (newProgress < 10) 10 else newProgress

        coffeeProgressView.setProgress(adjustedProgress)
    }

    override fun resetCoffeeViewStatus() {
        coffeeProgressView.setProgress(0)
        coffeeStatusTitle.text = getString(R.string.title_coffeeview_idle)
        coffeeStatusMessage.text = getString(R.string.message_coffeeview_idle)
        coffeeProgressView.animate()
                .alpha(0.2f)
                .start()
    }

    override fun showCancelCoffeeProgressPrompt() {
        alert {
            title(R.string.really_cancel_coffee_progress_title)
            message(R.string.really_cancel_coffee_progress_message)

            cancelButton()
            okButton { presenter.cancelCoffeeCountDown() }
        }.show()
    }
}