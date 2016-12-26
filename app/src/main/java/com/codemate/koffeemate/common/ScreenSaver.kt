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

package com.codemate.koffeemate.common

import android.app.Activity
import android.app.AlarmManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.view.View
import android.view.ViewGroup
import com.codemate.koffeemate.R
import org.jetbrains.anko.alarmManager
import org.jetbrains.anko.onClick
import java.util.*
import java.util.concurrent.TimeUnit

interface ScreenSaver {
    fun defer()
    fun start()
    fun stop()
}

open class AndroidScreenSaver constructor(private val activity: Activity) : ScreenSaver {
    private val ACTION_ENABLE_SCREEN_SAVER = "com.codemate.brewstat.ACTION_ENABLE_SCREEN_SAVER"
    private val ACTION_DISABLE_SCREEN_SAVER = "com.codemate.brewstat.ACTION_DISABLE_SCREEN_SAVER"

    private val receiver = object : BroadcastReceiver() {
        override fun onReceive(ctx: Context, intent: Intent) {
            if (isScreenSavingTime()) {
                screenOverlay.visibility = View.VISIBLE
            } else {
                screenOverlay.visibility = View.GONE
            }
        }
    }

    private val showScreenSaverRunnable = {
        if (isScreenSavingTime()) {
            screenOverlay.visibility = View.VISIBLE
        }
    }

    private lateinit var screenOverlay: View

    init {
        initializeOverlay()
    }

    override fun start() {
        registerScreenSaverReceiver()
        scheduleDailyAlarms()
    }

    override fun stop() {
        activity.unregisterReceiver(receiver)
    }

    override fun defer() {
        screenOverlay.removeCallbacks(showScreenSaverRunnable)
        showScreenSaverDelayedIfNecessary()
    }

    private fun showScreenSaverDelayedIfNecessary() {
        screenOverlay.removeCallbacks(showScreenSaverRunnable)
        screenOverlay.postDelayed(showScreenSaverRunnable, TimeUnit.MINUTES.toMillis(5))
    }

    private fun isScreenSavingTime(): Boolean {
        val calendar = Calendar.getInstance()
        calendar.timeInMillis = System.currentTimeMillis()

        val currentHour = calendar.get(Calendar.HOUR_OF_DAY)
        return !(currentHour > 8 && currentHour < 16)
    }

    private fun registerScreenSaverReceiver() {
        val intentFilter = IntentFilter(ACTION_ENABLE_SCREEN_SAVER)
        activity.registerReceiver(receiver, intentFilter)
    }

    private fun initializeOverlay() {
        val rootView = activity.window.decorView as ViewGroup
        screenOverlay = activity.layoutInflater.inflate(R.layout.view_screen_saver_overlay, rootView, false)
        screenOverlay.visibility = View.GONE
        rootView.addView(screenOverlay)

        screenOverlay.onClick {
            screenOverlay.visibility = View.GONE
            showScreenSaverDelayedIfNecessary()
        }
    }

    private fun scheduleDailyAlarms() {
        scheduleAlarm(ACTION_ENABLE_SCREEN_SAVER, 16)
        scheduleAlarm(ACTION_DISABLE_SCREEN_SAVER, 8)
    }

    private fun scheduleAlarm(action: String, hourOfDay: Int) {
        val alarmIntent = getIntent(action)
        val calendar = Calendar.getInstance()
        calendar.timeInMillis = System.currentTimeMillis()
        calendar.set(Calendar.HOUR_OF_DAY, hourOfDay)
        calendar.set(Calendar.MINUTE, 0)

        activity.alarmManager.setRepeating(
                AlarmManager.RTC_WAKEUP,
                calendar.timeInMillis,
                AlarmManager.INTERVAL_DAY,
                alarmIntent
        )
    }

    private fun getIntent(action: String): PendingIntent? {
        return PendingIntent.getBroadcast(
                activity,
                0,
                Intent(action),
                PendingIntent.FLAG_UPDATE_CURRENT
        )
    }
}