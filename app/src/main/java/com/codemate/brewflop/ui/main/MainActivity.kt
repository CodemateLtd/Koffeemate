package com.codemate.brewflop.ui.main

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.codemate.brewflop.BuildConfig
import com.codemate.brewflop.R
import com.codemate.brewflop.data.local.CoffeePreferences
import com.codemate.brewflop.data.network.SlackApi
import com.codemate.brewflop.data.network.SlackService
import com.codemate.brewflop.ui.secret.SecretSettingsActivity
import com.codemate.brewflop.util.extensions.hideStatusBar
import kotlinx.android.synthetic.main.activity_main.*
import org.jetbrains.anko.*
import java.util.concurrent.TimeUnit

class MainActivity : AppCompatActivity(), MainView {
    lateinit var presenter: MainPresenter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        hideStatusBar()

        val coffeePreferences = CoffeePreferences(this)
        val brewingProgressUpdater = BrewingProgressUpdater(
                brewingTimeMillis = TimeUnit.SECONDS.toMillis(15),
                totalSteps = 30
        )

        val slackApi = SlackService.getApi(SlackApi.BASE_URL)

        presenter = MainPresenter(coffeePreferences, brewingProgressUpdater, slackApi)
        presenter.attachView(this)

        coffeeProgressView.alpha = 0.2f
        coffeeProgressView.onClick {
            presenter.startCountDownForNewCoffee("Freshly brewed coffee available!")
        }

        coffeeProgressView.onLongClick {
            startActivity(intentFor<SecretSettingsActivity>())
            true
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        presenter.detachView()
    }

    override fun noChannelNameSet() {
        longToast(R.string.no_channel_name_set)
        startActivity(intentFor<SecretSettingsActivity>())
    }

    override fun newCoffeeIsComing() {
        coffeeStatusTitle.text = getString(R.string.coffee_is_coming_title)
        coffeeStatusMessage.text = getString(R.string.coffee_is_coming_message)
        coffeeProgressView.animate()
                .alpha(1f)
                .start()
    }

    override fun updateCoffeeProgress(newProgress: Int) {
        coffeeProgressView.setProgress(newProgress)
    }

    override fun newCoffeeAvailable() {
        toast("NEW COFFEE AVAILABLE!")
    }

    override fun noCoffeeAnyMore() {
        coffeeStatusTitle.text = getString(R.string.did_you_start_the_coffee_machine)
        coffeeStatusMessage.text = getString(R.string.touch_here_to_notify_when_coffee_ready)
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