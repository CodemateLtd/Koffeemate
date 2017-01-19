package com.codemate.koffeemate.data.local

import android.content.Context
import android.content.SharedPreferences
import com.codemate.koffeemate.R
import org.jetbrains.anko.defaultSharedPreferences

open class CoffeePreferences(ctx: Context) {
    val DEFAULT_COFFEE_BREWING_TIME_MINUTES = "7"

    var preferences: SharedPreferences = ctx.defaultSharedPreferences

    val coffeeBrewingTimeKey: String = ctx.getString(R.string.preference_coffee_brewing_time_key)
    val announcementChannelKey: String = ctx.getString(R.string.preference_coffee_announcement_slack_channel_key)
    val differentChannelForAccidentsKey: String = ctx.getString(R.string.preference_use_different_channel_for_accidents_key)
    val accidentChannelKey: String = ctx.getString(R.string.preference_coffee_accident_slack_channel_key)

    open fun getCoffeeBrewingTime(): Long {
        return preferences.getString(
                coffeeBrewingTimeKey,
                DEFAULT_COFFEE_BREWING_TIME_MINUTES
        ).toLong()
    }

    open fun isCoffeeAnnouncementChannelSet() = !getCoffeeAnnouncementChannel().isBlank()

    open fun getCoffeeAnnouncementChannel(): String {
        return preferences.getString(announcementChannelKey, null) ?: ""
    }

    open fun useDifferentChannelForAccidents() =
            preferences.getBoolean(differentChannelForAccidentsKey, false)

    open fun isAccidentChannelSet(): Boolean {
        if (useDifferentChannelForAccidents()) {
            return !getAccidentChannel().isBlank()
        }

        return isCoffeeAnnouncementChannelSet()
    }

    open fun getAccidentChannel(): String {
        if (useDifferentChannelForAccidents()) {
            return preferences.getString(accidentChannelKey, null) ?: ""
        }

        return preferences.getString(announcementChannelKey, null) ?: ""
    }
}