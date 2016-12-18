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

package com.codemate.brewflop.data.local

import android.content.Context
import android.content.SharedPreferences
import com.codemate.brewflop.BuildConfig
import com.codemate.brewflop.R
import org.jetbrains.anko.defaultSharedPreferences

open class CoffeePreferences(ctx: Context) {
    var preferences: SharedPreferences

    private val announcementChannelKey: String
    private val accidentChannelKey: String

    init {
        preferences = ctx.defaultSharedPreferences
        announcementChannelKey = ctx.getString(R.string.preference_coffee_announcement_slack_channel_key)
        accidentChannelKey = ctx.getString(R.string.preference_coffee_accident_slack_channel_key)
    }

    fun isCoffeeAnnouncementChannelSet() = !getCoffeeAnnouncementChannel().isBlank()

    fun getCoffeeAnnouncementChannel(): String {
        return preferences.getString(announcementChannelKey, null) ?: ""
    }

    fun isAccidentChannelSet() = !getAccidentChannel().isBlank()

    fun getAccidentChannel(): String {
        return preferences.getString(accidentChannelKey, null) ?: ""
    }
}