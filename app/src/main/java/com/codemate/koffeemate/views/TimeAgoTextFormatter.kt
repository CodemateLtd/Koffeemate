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

package com.codemate.koffeemate.views

import android.content.Context
import com.codemate.koffeemate.R
import java.util.concurrent.TimeUnit

class TimeAgoTextFormatter(private val ctx: Context) {
    fun getHowLongAgoText(millisecondsAgo: Long): String {
        if (isUnder(TimeUnit.MINUTES, millisecondsAgo)) {
            return ctx.getString(R.string.time_just_now)
        } else if (isUnder(TimeUnit.HOURS, millisecondsAgo)) {
            val timeDifferenceMinutes = TimeUnit.MILLISECONDS.toMinutes(millisecondsAgo)

            return getProperFormattedText(
                    timeDifferenceMinutes,
                    R.string.time_one_minute_ago,
                    R.string.time_n_minutes_ago
            )
        } else if (isUnder(TimeUnit.DAYS, millisecondsAgo)) {
            val timeDifferenceHours = TimeUnit.MILLISECONDS.toHours(millisecondsAgo)

            return getProperFormattedText(
                    timeDifferenceHours,
                    R.string.time_one_hour_ago,
                    R.string.time_n_hours_ago
            )
        } else if (isUnder(TimeUnit.DAYS, howMany = 7, what = millisecondsAgo)) {
            val timeDifferenceDays = TimeUnit.MILLISECONDS.toDays(millisecondsAgo)

            return getProperFormattedText(
                    timeDifferenceDays,
                    R.string.time_one_day_ago,
                    R.string.time_n_days_ago
            )
        } else {
            val timeDifferenceWeeks = TimeUnit.MILLISECONDS.toDays(millisecondsAgo) / 7

            return getProperFormattedText(
                    timeDifferenceWeeks,
                    R.string.time_one_week_ago,
                    R.string.time_n_weeks_ago
            )
        }
    }

    private fun isUnder(timeUnit: TimeUnit, what: Long, howMany: Long = 1): Boolean {
        return what < timeUnit.toMillis(howMany)
    }

    private fun getProperFormattedText(timeDifference: Long, oneTimeUnitAgoResource: Int, manyTimeUnitsAgoResource: Int): String {
        if (timeDifference <= 1) {
            return ctx.getString(oneTimeUnitAgoResource)
        }

        return ctx.getString(manyTimeUnitsAgoResource, timeDifference)
    }
}