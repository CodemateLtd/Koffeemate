/*
 * Copyright 2016 Codemate Ltd
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.codemate.brewflop.ui.main

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.view.WindowManager
import com.codemate.brewflop.BrewFlopApp
import com.codemate.brewflop.R
import com.codemate.brewflop.data.ScreenSaverManager
import com.codemate.brewflop.data.local.models.CoffeeBrewingEvent
import com.codemate.brewflop.ui.secretsettings.SecretSettingsActivity
import com.codemate.brewflop.ui.userselector.UserSelectorActivity
import kotlinx.android.synthetic.main.activity_main.*
import org.jetbrains.anko.*
import javax.inject.Inject

class MainActivity : AppCompatActivity(), MainView {
    @Inject
    lateinit var presenter: MainPresenter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        ScreenSaverManager.attach(this)
        BrewFlopApp.appComponent.inject(this)

        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_FULLSCREEN

        presenter.attachView(this)
        setUpListeners()
    }

    fun setUpListeners() {
        coffeeProgressView.onClick {
            val newCoffeeMessage = getString(R.string.new_coffee_available)
            presenter.startDelayedCoffeeAnnouncement(newCoffeeMessage)
        }

        coffeeProgressView.onLongClick {
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