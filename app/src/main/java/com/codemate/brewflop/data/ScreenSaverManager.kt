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

package com.codemate.brewflop.data

import android.app.Activity
import android.app.AlarmManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.view.View
import android.view.ViewGroup
import com.codemate.brewflop.R
import org.jetbrains.anko.alarmManager
import org.jetbrains.anko.onClick
import java.util.*

class ScreenSaverManager private constructor(val activity: Activity) {
    private val ACTION_ENABLE_SCREENSAVER = "com.codemate.brewstat.ACTION_ENABLE_SCREENSAVER"
    private val ACTION_DISABLE_SCREENSAVER = "com.codemate.brewstat.ACTION_DISABLE_SCREENSAVER"

    companion object {
        fun attach(activity: Activity) = ScreenSaverManager(activity)
    }

    private val alarmManager: AlarmManager
    private lateinit var screenOverlay: View
    private val receiver = object : BroadcastReceiver() {
        override fun onReceive(ctx: Context, intent: Intent) {
            when (intent.action) {
                ACTION_DISABLE_SCREENSAVER -> screenOverlay.visibility = View.GONE
                ACTION_ENABLE_SCREENSAVER -> screenOverlay.visibility = View.VISIBLE
            }
        }
    }

    init {
        alarmManager = activity.alarmManager

        registerScreenSaverReceiver()
        initializeOverlay()
        scheduleDailyAlarms()
    }

    private fun registerScreenSaverReceiver() {
        val intentFilter = IntentFilter()
        intentFilter.addAction(ACTION_ENABLE_SCREENSAVER)
        intentFilter.addAction(ACTION_DISABLE_SCREENSAVER)
        activity.registerReceiver(receiver, intentFilter)
    }

    private fun initializeOverlay() {
        val rootView = activity.window.decorView as ViewGroup
        screenOverlay = activity.layoutInflater.inflate(R.layout.view_screen_saver_overlay, null)
        screenOverlay.visibility = View.GONE
        rootView.addView(screenOverlay)

        screenOverlay.onClick { screenOverlay.visibility = View.GONE }
    }

    private fun scheduleDailyAlarms() {
        scheduleAlarm(ACTION_DISABLE_SCREENSAVER, 6)
        scheduleAlarm(ACTION_ENABLE_SCREENSAVER, 18)
    }

    private fun scheduleAlarm(action: String, hourOfDay: Int) {
        val intent = Intent(action)
        val alarmIntent = PendingIntent.getBroadcast(activity, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT)

        val calendar = Calendar.getInstance()
        calendar.timeInMillis = System.currentTimeMillis()
        calendar.set(Calendar.HOUR_OF_DAY, hourOfDay)
        calendar.set(Calendar.MINUTE, 0)

        alarmManager.setRepeating(
                AlarmManager.RTC_WAKEUP,
                calendar.timeInMillis,
                AlarmManager.INTERVAL_DAY,
                alarmIntent
        )
    }
}