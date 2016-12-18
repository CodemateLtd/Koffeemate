package com.codemate.brewflop.views

import android.content.Context
import android.util.AttributeSet
import android.widget.FrameLayout
import com.codemate.brewflop.R
import kotlinx.android.synthetic.main.view_coffee_progress.view.*

class CoffeeProgressView(ctx: Context, attrs: AttributeSet) : FrameLayout(ctx, attrs) {
    init {
        inflate(ctx, R.layout.view_coffee_progress, this)
    }

    fun setProgress(newProgress: Int) {
        // For some reason, the CircularFillableLoaders library uses inverted
        // progress values: 0 means full and 100 means empty.
        coffeeFillableLoader.setProgress(100 - newProgress)
    }
}