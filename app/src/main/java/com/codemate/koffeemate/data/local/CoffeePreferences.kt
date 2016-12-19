package com.codemate.koffeemate.data.local

import android.content.Context
import android.content.SharedPreferences
import com.codemate.koffeemate.BuildConfig
import com.codemate.koffeemate.R
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