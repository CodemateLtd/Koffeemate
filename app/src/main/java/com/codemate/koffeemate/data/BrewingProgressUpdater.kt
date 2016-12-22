package com.codemate.koffeemate.data

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
class BrewingProgressUpdater(
        brewingTimeMillis: Long,
        private val totalSteps: Int) : Runnable {
    var updateHandler: Handler
    val updateInterval: Long

    var isUpdating = false
    var currentStep = 0

    var updateListener: ((Int) -> Unit)? = null
    var completeListener: (() -> Unit)? = null

    init {
        updateHandler = Handler()
        updateInterval = brewingTimeMillis / totalSteps
    }

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