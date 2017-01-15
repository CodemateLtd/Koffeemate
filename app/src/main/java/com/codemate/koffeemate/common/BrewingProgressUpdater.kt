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

import android.os.Handler

/**
 * Class responsible for updating the CircularFillableLoader on
 * the main screen from empty to full state, since the library
 * doesn't support it out of the box.
 *
 * This is generalized enough for other purposes as well.
 *
 * See MainPresenter & BrewingProgressUpdateTest for usage.
 */
open class BrewingProgressUpdater(
        brewingTimeMillis: Long,
        private val totalSteps: Int) : Runnable {
    var updateHandler: Handler = Handler()
    val updateInterval: Long = brewingTimeMillis / totalSteps

    var isUpdating = false
    var currentStep = 0

    var updateListener: ((Int) -> Unit)? = null
    var completeListener: (() -> Unit)? = null

    fun startUpdating(updateListener: (Int) -> Unit, completeListener: () -> Unit) {
        if (!isUpdating) {
            this.updateListener = updateListener
            this.completeListener = completeListener

            isUpdating = true
            updateListener(currentStep)
            updateHandler.postDelayed(this, updateInterval)
        }
    }

    fun reset() {
        updateListener = null
        completeListener = null
        isUpdating = false
        currentStep = 0
        updateHandler.removeCallbacks(this)
    }

    override fun run() {
        if (!isUpdating) {
            return
        }

        currentStep++

        if (currentStep >= totalSteps) {
            completeListener?.invoke()
            reset()
        } else {
            updateListener?.invoke(calculateCurrentProgress())
            updateHandler.postDelayed(this, updateInterval)
        }
    }

    fun calculateCurrentProgress() = Math.round((currentStep / (totalSteps * 1.0)) * 100.0).toInt()
}