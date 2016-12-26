package com.codemate.koffeemate.views

import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent
import android.widget.FrameLayout
import com.codemate.koffeemate.R
import kotlinx.android.synthetic.main.view_coffee_progress.view.*
import org.jetbrains.anko.onTouch

class CoffeeProgressView(ctx: Context, attrs: AttributeSet) : FrameLayout(ctx, attrs) {
    init {
        inflate(ctx, R.layout.view_coffee_progress, this)
        initializeTouchListener()
    }

    private fun initializeTouchListener() {
        onTouch { view, motionEvent ->
            when(motionEvent.action) {
                MotionEvent.ACTION_DOWN -> animateTouchDown()
                MotionEvent.ACTION_UP -> animateTouchUp()
            }

            return@onTouch false
        }
    }

    private fun animateTouchDown() {
        animate().scaleX(0.9f)
                .scaleY(0.9f)
                .setDuration(100)
                .start()
    }

    private fun animateTouchUp() {
        animate().scaleX(1f)
                .scaleY(1f)
                .setDuration(100)
                .start()
    }

    fun setProgress(newProgress: Int) {
        // For some reason, the CircularFillableLoaders library uses inverted
        // progress values: 0 means full and 100 means empty.
        coffeeFillableLoader.setProgress(100 - newProgress)
    }
}