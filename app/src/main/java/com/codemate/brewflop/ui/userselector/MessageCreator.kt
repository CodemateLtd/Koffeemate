package com.codemate.brewflop.ui.userselector

import android.content.Context
import com.codemate.brewflop.R
import com.codemate.brewflop.data.local.BrewFailureLogger
import com.codemate.brewflop.data.network.model.User

class MessageCreator(context: Context) {
    val nameIsLeading = context.getString(R.string.name_is_leading_fmt)
    val nameResetedCounter = context.getString(R.string.name_reseted_counter_was_at_n_days_fmt)
    val namesNthBrewingAccident = context.getString(R.string.names_nth_brewing_accident_fmt)

    fun createMessage(
            user: User,
            failureLogger: BrewFailureLogger,
            incidentFreeDays: Int,
            onCompleteListener: (String) -> Unit) {
        failureLogger.getUserWithMostFailures { leadingUserId ->
            var message = nameResetedCounter.format(user.profile.realName, incidentFreeDays)
            val currentAccidentCount = failureLogger.getFailureCountForUser(user).toInt()

            if (leadingUserId == user.id) {
                message += "\n" + nameIsLeading.format(user.profile.firstName, currentAccidentCount)
            } else {
                message += "\n" + namesNthBrewingAccident.format(user.profile.firstName, dayOfMonthSuffix(currentAccidentCount))
            }

            onCompleteListener(message)
        }
    }

    fun dayOfMonthSuffix(day: Int): String {
        if (day >= 11 && day <= 13) {
            return "${day}th"
        }
        return when (day % 10) {
            1 -> "${day}st"
            2 -> "${day}nd"
            3 -> "${day}rd"
            else -> "${day}th"
        }
    }
}