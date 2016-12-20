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
import com.codemate.koffeemate.views.TimeAgoTextView.Companion.getHowLongAgoText
import com.codemate.koffeemate.views.TimeAgoTextView.Companion.getUpdateInterval
import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.whenever
import org.hamcrest.core.IsEqual.equalTo
import org.junit.Assert.assertThat
import org.junit.Before
import org.junit.Test
import java.util.concurrent.TimeUnit

class TimeAgoTextViewTest {
    private val MINUTE_IN_MILLIS = TimeUnit.MINUTES.toMillis(1)
    private val HOUR_IN_MILLIS = TimeUnit.HOURS.toMillis(1)
    private val DAY_IN_MILLIS = TimeUnit.DAYS.toMillis(1)
    private val WEEK_IN_MILLIS = DAY_IN_MILLIS * 7

    lateinit var mockContext: Context

    @Before
    fun setUp() {
        mockContext = mock<Context>()

        // Yup, avoiding those Android instrumentation tests at any cost ¯\_(ツ)_/¯
        returnStringForStringResource(R.string.time_just_now, "just now")
        returnStringForStringResource(R.string.time_one_minute_ago, "a minute ago")
        returnStringForStringResource(R.string.time_n_minutes_ago, 59, "%d minutes ago")
        returnStringForStringResource(R.string.time_one_hour_ago, "an hour ago")
        returnStringForStringResource(R.string.time_n_hours_ago, 23, "%d hours ago")
        returnStringForStringResource(R.string.time_one_day_ago, "a day ago")
        returnStringForStringResource(R.string.time_n_days_ago, 6, "%d days ago")
        returnStringForStringResource(R.string.time_one_week_ago, "a week ago")
        returnStringForStringResource(R.string.time_n_weeks_ago, 3, "%d weeks ago")
    }

    @Test
    fun updateIntervalCalculationTests() {
        assertThat(getUpdateInterval(MINUTE_IN_MILLIS), equalTo(MINUTE_IN_MILLIS))
        assertThat(getUpdateInterval(MINUTE_IN_MILLIS * 59), equalTo(MINUTE_IN_MILLIS))

        assertThat(getUpdateInterval(HOUR_IN_MILLIS), equalTo(HOUR_IN_MILLIS))
        assertThat(getUpdateInterval(HOUR_IN_MILLIS * 23), equalTo(HOUR_IN_MILLIS))

        assertThat(getUpdateInterval(DAY_IN_MILLIS), equalTo(DAY_IN_MILLIS))
        assertThat(getUpdateInterval(DAY_IN_MILLIS * 6), equalTo(DAY_IN_MILLIS))

        assertThat(getUpdateInterval(WEEK_IN_MILLIS), equalTo(WEEK_IN_MILLIS))
        assertThat(getUpdateInterval(WEEK_IN_MILLIS * 3), equalTo(WEEK_IN_MILLIS))
    }

    @Test
    fun justNowTests() {
        val justNowOne = getHowLongAgoText(mockContext, 0)
        assertThat(justNowOne, equalTo("just now"))

        val justNowTwo = getHowLongAgoText(mockContext, TimeUnit.SECONDS.toMillis(59))
        assertThat(justNowTwo, equalTo("just now"))
    }

    @Test
    fun minuteTests() {
        val oneMinuteAgo = getHowLongAgoText(mockContext, TimeUnit.MINUTES.toMillis(1))
        assertThat(oneMinuteAgo, equalTo("a minute ago"))

        val minutesAgo = getHowLongAgoText(mockContext, TimeUnit.MINUTES.toMillis(59))
        assertThat(minutesAgo, equalTo("59 minutes ago"))
    }

    @Test
    fun hourTests() {
        val oneHourAgo = getHowLongAgoText(mockContext, TimeUnit.HOURS.toMillis(1))
        assertThat(oneHourAgo, equalTo("an hour ago"))

        val hoursAgo = getHowLongAgoText(mockContext, TimeUnit.HOURS.toMillis(23))
        assertThat(hoursAgo, equalTo("23 hours ago"))
    }

    @Test
    fun dayTests() {
        val oneDayAgo = getHowLongAgoText(mockContext, TimeUnit.DAYS.toMillis(1))
        assertThat(oneDayAgo, equalTo("a day ago"))

        val daysAgo = getHowLongAgoText(mockContext, TimeUnit.DAYS.toMillis(6))
        assertThat(daysAgo, equalTo("6 days ago"))
    }

    @Test
    fun weekTests() {
        val oneWeekAgo = getHowLongAgoText(mockContext, TimeUnit.DAYS.toMillis(7))
        assertThat(oneWeekAgo, equalTo("a week ago"))

        val weeksAgo = getHowLongAgoText(mockContext, TimeUnit.DAYS.toMillis(27))
        assertThat(weeksAgo, equalTo("3 weeks ago"))
    }

    private fun returnStringForStringResource(stringRes: Int, returnedValue: String) {
        whenever(mockContext.getString(stringRes)).thenReturn(returnedValue)
    }

    private fun returnStringForStringResource(stringRes: Int, howMany: Long, returnedValue: String) {
        whenever(mockContext.getString(stringRes, howMany))
                .thenReturn(String.format(returnedValue, howMany))
    }
}