package com.codemate.brewflop.data.local

import android.content.Context
import android.content.SharedPreferences
import com.codemate.brewflop.BuildConfig
import com.codemate.brewflop.R
import org.jetbrains.anko.defaultSharedPreferences

open class CoffeePreferences(ctx: Context) {
    var preferences: SharedPreferences
    private val channelNameKey: String

    init {
        preferences = ctx.defaultSharedPreferences
        channelNameKey = ctx.getString(R.string.preference_coffee_announcement_slack_channel_key)
    }

    fun isCoffeeAnnouncementChannelSet() = !getCoffeeAnnouncementChannel().isBlank()

    fun getCoffeeAnnouncementChannel(): String {
        return preferences.getString(channelNameKey, null) ?: ""
    }
}