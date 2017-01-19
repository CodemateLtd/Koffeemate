package com.codemate.koffeemate.views

import android.content.Context
import android.graphics.Bitmap
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import android.widget.FrameLayout
import com.codemate.koffeemate.R
import kotlinx.android.synthetic.main.view_coffee_progress.view.*
import org.jetbrains.anko.*

class CoffeeProgressView(ctx: Context, attrs: AttributeSet) : FrameLayout(ctx, attrs) {
    init {
        padding = ctx.dip(16)
        clipToPadding = false

        inflate(ctx, R.layout.view_coffee_progress, this)
        initializeTouchListener()
    }

    fun setOnCoffeePotClickListener(listener: (View?) -> Unit) {
        coffeePotButton.onClick(listener)
    }

    fun setOnUserSetterClickListener(listener: (View?) -> Unit) {
        userSetterButton.onClick(listener)
    }

    fun setCoffeeIncoming() {
        animate().alpha(1f).start()
        userSetterButton.show()
    }

    fun reset() {
        animate().alpha(0.2f).start()
        userSetterButton.reset()
    }

    fun setProgress(newProgress: Int) {
        // For some reason, the CircularFillableLoaders library uses inverted
        // progress values: 0 means full and 100 means empty.
        coffeeFillableLoader.setProgress(100 - newProgress)
    }

    private fun initializeTouchListener() {
        onTouch { view, motionEvent ->
            when(motionEvent.action) {
                MotionEvent.ACTION_DOWN -> animateTouch(0.9f)
                MotionEvent.ACTION_UP -> animateTouch(1.0f)
            }

            return@onTouch false
        }
    }

    private fun animateTouch(scale: Float) {
        animate().scaleX(scale)
                .scaleY(scale)
                .setDuration(100)
                .start()
    }
}