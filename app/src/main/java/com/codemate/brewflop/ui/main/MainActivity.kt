package com.codemate.brewflop.ui.main

import android.content.Intent
import android.os.Bundle
import android.support.v4.content.LocalBroadcastManager
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.view.WindowManager
import com.codemate.brewflop.DayCountUpdater
import com.codemate.brewflop.R
import com.codemate.brewflop.ui.secret.SecretSettingsActivity
import com.codemate.brewflop.ui.userselector.UserSelectorActivity
import kotlinx.android.synthetic.main.activity_main.*
import org.jetbrains.anko.onClick
import org.jetbrains.anko.onLongClick

class MainActivity : AppCompatActivity() {
    private lateinit var dayCountUpdater: DayCountUpdater
    private lateinit var dayUpdateListener: DayUpdateListener

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        hideStatusBar()

        dayCountUpdater = DayCountUpdater(this)
        dayUpdateListener = DayUpdateListener(
                LocalBroadcastManager.getInstance(this),
                DayUpdateListener.OnDayChangedListener { updateDayCountText() }
        )

        resetButton.onClick {
            val intent = Intent(this@MainActivity, UserSelectorActivity::class.java)
            startActivity(intent)
        }

        resetButton.onLongClick {
            val intent = Intent(this@MainActivity, SecretSettingsActivity::class.java)
            startActivity(intent)

            true
        }
    }

    fun updateDayCountText() {
        val dayCount = dayCountUpdater.dayCount
        val formattedText = resources.getQuantityString(R.plurals.number_of_days, dayCount, dayCount)

        daysSinceLastIncident.text = formattedText
    }

    override fun onResume() {
        super.onResume()

        dayUpdateListener.listenForDayChanges()
        updateDayCountText()
    }

    override fun onStop() {
        super.onStop()

        dayUpdateListener.stopListeningForDayChanges()
    }

    private fun hideStatusBar() {
        val uiOptions = View.SYSTEM_UI_FLAG_FULLSCREEN
        window.decorView.systemUiVisibility = uiOptions
    }
}
