/*
 * Copyright 2017 Codemate Ltd
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

import org.hamcrest.core.IsEqual.equalTo
import org.junit.Assert.assertThat
import org.junit.Before
import org.junit.Test
import rx.observers.TestSubscriber
import rx.schedulers.Schedulers
import rx.schedulers.TestScheduler
import java.util.concurrent.TimeUnit

class BrewingProgressUpdaterTest {
    lateinit var testScheduler: TestScheduler
    lateinit var updater: BrewingProgressUpdater
    lateinit var testSubscriber: TestSubscriber<Int>

    @Before
    fun setUp() {
        testScheduler = Schedulers.test()
        updater = BrewingProgressUpdater(testScheduler)
        testSubscriber = TestSubscriber()
    }

    @Test
    fun start_WithOneSecond_HasTwoSteps() {
        updater.start(TimeUnit.SECONDS.toMillis(1)).subscribe(testSubscriber)

        testScheduler.advanceTimeBy(1, TimeUnit.SECONDS)
        testSubscriber.assertValueCount(2)
    }

    @Test
    fun start_WithThreeSeconds_HasSixSteps() {
        updater.start(TimeUnit.SECONDS.toMillis(3)).subscribe(testSubscriber)

        testScheduler.advanceTimeBy(3, TimeUnit.SECONDS)
        testSubscriber.assertValueCount(6)
    }

    @Test
    fun start_RunsProperlyAndCompletes() {
        updater.start(TimeUnit.SECONDS.toMillis(2)).subscribe(testSubscriber)

        testScheduler.advanceTimeBy(500, TimeUnit.MILLISECONDS)
        testSubscriber.assertValueCount(1)
        assertThat(testSubscriber.onNextEvents[0], equalTo(0))

        testScheduler.advanceTimeBy(500, TimeUnit.MILLISECONDS)
        testSubscriber.assertValueCount(2)
        assertThat(testSubscriber.onNextEvents[1], equalTo(25))

        testScheduler.advanceTimeBy(500, TimeUnit.MILLISECONDS)
        testSubscriber.assertValueCount(3)
        assertThat(testSubscriber.onNextEvents[2], equalTo(50))

        testScheduler.advanceTimeBy(500, TimeUnit.MILLISECONDS)
        testSubscriber.assertValueCount(4)
        assertThat(testSubscriber.onNextEvents[3], equalTo(75))

        testSubscriber.assertCompleted()
    }
}