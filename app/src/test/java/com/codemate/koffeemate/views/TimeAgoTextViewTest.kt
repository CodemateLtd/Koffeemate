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

import com.codemate.koffeemate.views.TimeAgoTextView.Companion.getUpdateInterval
import org.hamcrest.core.IsEqual.equalTo
import org.junit.Assert.assertThat
import org.junit.Test
import java.util.concurrent.TimeUnit

class TimeAgoTextViewTest {
    private val MINUTE_IN_MILLIS = TimeUnit.MINUTES.toMillis(1)
    private val HOUR_IN_MILLIS = TimeUnit.HOURS.toMillis(1)
    private val DAY_IN_MILLIS = TimeUnit.DAYS.toMillis(1)
    private val WEEK_IN_MILLIS = DAY_IN_MILLIS * 7

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
}