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

package com.codemate.koffeemate.common

import rx.Observable
import rx.Scheduler
import rx.schedulers.Schedulers
import java.util.concurrent.TimeUnit

/**
 * Class responsible for updating the CircularFillableLoader on
 * the main screen from empty to full state, since the library
 * doesn't support it out of the box.
 */
open class BrewingProgressUpdater(
        private val intervalScheduler: Scheduler = Schedulers.computation(),
        private val observerScheduler: Scheduler = Schedulers.immediate()
) {
    open fun start(brewingTimeMillis: Long): Observable<Int> {
        val totalSteps = (TimeUnit.MILLISECONDS.toSeconds(brewingTimeMillis) * 2).toInt()
        val interval = brewingTimeMillis / totalSteps

        return Observable
                .interval(interval, TimeUnit.MILLISECONDS, intervalScheduler)
                .observeOn(observerScheduler)
                .take(totalSteps)
                .map { currentStep -> convertToPercent(currentStep, totalSteps) }
    }

    private fun convertToPercent(currentStep: Long, totalSteps: Int): Int {
        return Math.round((currentStep / (totalSteps * 1.0)) * 100.0).toInt()
    }
}