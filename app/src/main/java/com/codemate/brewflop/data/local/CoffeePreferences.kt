package com.codemate.brewflop.data.local

import android.content.Context
import android.content.SharedPreferences
import com.codemate.brewflop.R
import org.jetbrains.anko.defaultSharedPreferences

open class CoffeePreferences(ctx: Context) {
    var preferences: SharedPreferences
    private val channelNameKey: String

    init {
        preferences = ctx.defaultSharedPreferences
        channelNameKey = ctx.getString(R.string.pref_key_coffee_announcement_slack_channel)
    }

    fun isChannelNameSet() = !getChannelName().isBlank()

    fun getChannelName(): String {
        return preferences.getString(channelNameKey, null) ?: ""
    }
}