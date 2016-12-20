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
import android.text.format.DateUtils
import android.util.AttributeSet
import android.widget.TextView
import com.codemate.koffeemate.R
import java.util.*
import java.util.concurrent.TimeUnit

class TimeAgoTextView : TextView, Runnable {
    private var customText: String? = null
    private var time: Long = -1

    constructor(context: Context) : super(context) {
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        val ta = context.obtainStyledAttributes(attrs, R.styleable.TimeAgoTextView, 0, 0)

        try {
            customText = ta.getString(R.styleable.TimeAgoTextView_tatv_customText)

            if (customText == null) {
                customText = "%s"
            }
        } finally {
            ta.recycle()
        }
    }

    fun setTime(date: Date) {
        setTime(date.time)
    }

    fun setTime(time: Long) {
        this.time = time
        startUpdatingIfNecessary()
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        startUpdatingIfNecessary()
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        removeCallbacks(this)
    }

    private fun startUpdatingIfNecessary() {
        if (time != -1L) {
            removeCallbacks(this)
            post(this)
        }
    }

    override fun run() {
        val elapsedTime = System.currentTimeMillis() - time

        val timeAgo = getHowLongAgoText(context, elapsedTime)
        text = String.format(customText!!, timeAgo)

        postDelayed(this, getUpdateInterval(elapsedTime))
    }

    companion object {
        internal fun getUpdateInterval(elapsedTime: Long): Long {
            if (elapsedTime < DateUtils.HOUR_IN_MILLIS) {
                return DateUtils.MINUTE_IN_MILLIS
            } else if (elapsedTime < DateUtils.DAY_IN_MILLIS) {
                return DateUtils.HOUR_IN_MILLIS
            } else if (elapsedTime < DateUtils.WEEK_IN_MILLIS) {
                return DateUtils.DAY_IN_MILLIS
            } else {
                return DateUtils.WEEK_IN_MILLIS
            }
        }

        // TODO: Improve this monster somehow if possible
        internal fun getHowLongAgoText(context: Context, timeDifferenceMs: Long): String {
            if (timeDifferenceMs < TimeUnit.MINUTES.toMillis(1)) {
                return context.getString(R.string.time_just_now)
            } else if (timeDifferenceMs < TimeUnit.HOURS.toMillis(1)) {
                val timeDifferenceMinutes = TimeUnit.MILLISECONDS.toMinutes(timeDifferenceMs)

                if (timeDifferenceMinutes <= 1) {
                    return context.getString(R.string.time_one_minute_ago)
                } else {
                    return context.getString(R.string.time_n_minutes_ago, timeDifferenceMinutes)
                }
            } else if (timeDifferenceMs < TimeUnit.DAYS.toMillis(1)) {
                val timeDifferenceHours = TimeUnit.MILLISECONDS.toHours(timeDifferenceMs)

                if (timeDifferenceHours <= 1) {
                    return context.getString(R.string.time_one_hour_ago)
                } else {
                    return context.getString(R.string.time_n_hours_ago, timeDifferenceHours)
                }
            } else if (timeDifferenceMs < TimeUnit.DAYS.toMillis(7)) {
                val timeDifferenceDays = TimeUnit.MILLISECONDS.toDays(timeDifferenceMs)

                if (timeDifferenceDays <= 1) {
                    return context.getString(R.string.time_one_day_ago)
                } else {
                    return context.getString(R.string.time_n_days_ago, timeDifferenceDays)
                }
            } else {
                val timeDifferenceWeeks = TimeUnit.MILLISECONDS.toDays(timeDifferenceMs) / 7

                if (timeDifferenceWeeks <= 1) {
                    return context.getString(R.string.time_one_week_ago)
                } else {
                    return context.getString(R.string.time_n_weeks_ago, timeDifferenceWeeks)
                }
            }
        }
    }
}
