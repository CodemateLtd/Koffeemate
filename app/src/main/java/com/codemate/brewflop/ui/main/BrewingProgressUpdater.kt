package com.codemate.brewflop.ui.main

import android.os.Handler

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