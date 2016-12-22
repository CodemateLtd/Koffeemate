package com.codemate.koffeemate.data.local

import android.content.Context
import android.content.SharedPreferences
import com.codemate.koffeemate.BuildConfig
import com.codemate.koffeemate.R
import org.jetbrains.anko.defaultSharedPreferences

open class CoffeePreferences(ctx: Context) {
    var preferences: SharedPreferences

    val announcementChannelKey: String
    val differentChannelForAccidentsKey: String
    val accidentChannelKey: String

    init {
        preferences = ctx.defaultSharedPreferences
        announcementChannelKey = ctx.getString(R.string.preference_coffee_announcement_slack_channel_key)
        differentChannelForAccidentsKey = ctx.getString(R.string.preference_use_different_channel_for_accidents_key)
        accidentChannelKey = ctx.getString(R.string.preference_coffee_accident_slack_channel_key)
    }

    fun isCoffeeAnnouncementChannelSet() = !getCoffeeAnnouncementChannel().isBlank()

    fun getCoffeeAnnouncementChannel(): String {
        return preferences.getString(announcementChannelKey, null) ?: ""
    }

    fun useDifferentChannelForAccidents() =
            preferences.getBoolean(differentChannelForAccidentsKey, false)

    fun isAccidentChannelSet(): Boolean {
        if (useDifferentChannelForAccidents()) {
            return !getAccidentChannel().isBlank()
        }

        return isCoffeeAnnouncementChannelSet()
    }

    fun getAccidentChannel(): String {
        if (useDifferentChannelForAccidents()) {
            return preferences.getString(accidentChannelKey, null) ?: ""
        }

        return preferences.getString(announcementChannelKey, null) ?: ""
    }
}