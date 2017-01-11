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

package com.codemate.koffeemate.views

import android.content.Context
import android.util.AttributeSet
import android.view.animation.OvershootInterpolator
import com.codemate.koffeemate.R
import de.hdodenhof.circleimageview.CircleImageView
import org.jetbrains.anko.imageResource

class UserSetterButton(ctx: Context, attrs: AttributeSet) : CircleImageView(ctx, attrs) {
    private val EMPTY_IMAGE_RESOURCE = R.drawable.ic_add_user

    init {
        imageResource = EMPTY_IMAGE_RESOURCE
        alpha = 0f
        scaleX = 0f
        scaleY = 0f
    }

    fun show() {
        animateVisibility(1f)
    }

    fun reset() {
        animateVisibility(0f)
    }

    fun animateVisibility(value: Float) {
        animate().alpha(value)
                .scaleX(value)
                .scaleY(value)
                .setInterpolator(OvershootInterpolator(3f))
                .setStartDelay(100)
                .setDuration(300)
                .start()
    }
}