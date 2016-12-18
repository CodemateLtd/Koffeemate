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