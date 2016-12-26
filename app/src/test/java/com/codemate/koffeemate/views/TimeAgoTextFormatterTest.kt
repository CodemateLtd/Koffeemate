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
import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.whenever
import org.hamcrest.core.IsEqual
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import java.util.concurrent.TimeUnit

class TimeAgoTextFormatterTest {
    lateinit var mockContext: Context
    lateinit var formatter: TimeAgoTextFormatter

    @Before
    fun setUp() {
        mockContext = mock<Context>()
        formatter = TimeAgoTextFormatter(mockContext)

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
    fun justNowTests() {
        val justNowOne = formatter.getHowLongAgoText(0)
        Assert.assertThat(justNowOne, IsEqual.equalTo("just now"))

        val justNowTwo = formatter.getHowLongAgoText(TimeUnit.SECONDS.toMillis(59))
        Assert.assertThat(justNowTwo, IsEqual.equalTo("just now"))
    }

    @Test
    fun minuteTests() {
        val oneMinuteAgo = formatter.getHowLongAgoText(TimeUnit.MINUTES.toMillis(1))
        Assert.assertThat(oneMinuteAgo, IsEqual.equalTo("a minute ago"))

        val minutesAgo = formatter.getHowLongAgoText(TimeUnit.MINUTES.toMillis(59))
        Assert.assertThat(minutesAgo, IsEqual.equalTo("59 minutes ago"))
    }

    @Test
    fun hourTests() {
        val oneHourAgo = formatter.getHowLongAgoText(TimeUnit.HOURS.toMillis(1))
        Assert.assertThat(oneHourAgo, IsEqual.equalTo("an hour ago"))

        val hoursAgo = formatter.getHowLongAgoText(TimeUnit.HOURS.toMillis(23))
        Assert.assertThat(hoursAgo, IsEqual.equalTo("23 hours ago"))
    }

    @Test
    fun dayTests() {
        val oneDayAgo = formatter.getHowLongAgoText(TimeUnit.DAYS.toMillis(1))
        Assert.assertThat(oneDayAgo, IsEqual.equalTo("a day ago"))

        val daysAgo = formatter.getHowLongAgoText(TimeUnit.DAYS.toMillis(6))
        Assert.assertThat(daysAgo, IsEqual.equalTo("6 days ago"))
    }

    @Test
    fun weekTests() {
        val oneWeekAgo = formatter.getHowLongAgoText(TimeUnit.DAYS.toMillis(7))
        Assert.assertThat(oneWeekAgo, IsEqual.equalTo("a week ago"))

        val weeksAgo = formatter.getHowLongAgoText(TimeUnit.DAYS.toMillis(27))
        Assert.assertThat(weeksAgo, IsEqual.equalTo("3 weeks ago"))
    }

    private fun returnStringForStringResource(stringRes: Int, returnedValue: String) {
        whenever(mockContext.getString(stringRes)).thenReturn(returnedValue)
    }

    private fun returnStringForStringResource(stringRes: Int, howMany: Long, returnedValue: String) {
        whenever(mockContext.getString(stringRes, howMany))
                .thenReturn(String.format(returnedValue, howMany))
    }
}