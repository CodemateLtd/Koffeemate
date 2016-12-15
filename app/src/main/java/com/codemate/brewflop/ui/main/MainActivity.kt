package com.codemate.brewflop.ui.main

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.view.WindowManager
import com.codemate.brewflop.R
import com.codemate.brewflop.data.local.CoffeePreferences
import com.codemate.brewflop.data.network.SlackApi
import com.codemate.brewflop.data.network.SlackService
import com.codemate.brewflop.ui.secret.SecretSettingsActivity
import kotlinx.android.synthetic.main.activity_main.*
import org.jetbrains.anko.*
import java.util.concurrent.TimeUnit

class MainActivity : AppCompatActivity(), MainView {
    lateinit var presenter: MainPresenter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_FULLSCREEN

        val coffeePreferences = CoffeePreferences(this)
        val brewingProgressUpdater = BrewingProgressUpdater(
                brewingTimeMillis = TimeUnit.MINUTES.toMillis(6),
                totalSteps = 30
        )

        val slackApi = SlackService.getApi(SlackApi.BASE_URL)

        presenter = MainPresenter(coffeePreferences, brewingProgressUpdater, slackApi)
        presenter.attachView(this)

        coffeeProgressView.alpha = 0.2f
        coffeeProgressView.onClick {
            presenter.startCountDownForNewCoffee("@channel Freshly brewed coffee available!")
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
        val adjustedProgress = if (newProgress < 10) 10 else newProgress

        coffeeProgressView.setProgress(adjustedProgress)
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