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

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.view.animation.AccelerateDecelerateInterpolator
import com.codemate.koffeemate.R
import de.hdodenhof.circleimageview.CircleImageView
import org.jetbrains.anko.imageResource

class UserSetterButton(ctx: Context, attrs: AttributeSet) : CircleImageView(ctx, attrs) {
    private val EMPTY_IMAGE_RESOURCE = R.drawable.ic_add_user

    init {
        alpha = 0f
        scaleX = 0f
        scaleY = 0f
        reset()
    }

    fun show() {
        visibility = View.VISIBLE
        isClickable = true
        animateVisibility(1f)
    }

    fun hide() {
        animateVisibility(0f, object : AnimatorListenerAdapter(){
            override fun onAnimationEnd(animation: Animator?) {
                reset()
            }
        })
    }

    fun animateVisibility(value: Float, listener: Animator.AnimatorListener? = null) {
        animate().alpha(value)
                .scaleX(value)
                .scaleY(value)
                .setInterpolator(AccelerateDecelerateInterpolator())
                .setStartDelay(100)
                .setDuration(200)
                .setListener(listener)
                .start()
    }

    fun clearUser() {
        animate().alpha(0.1f)
                .scaleX(0.1f)
                .scaleY(0.1f)
                .rotation(180f)
                .withEndAction {
                    imageResource = EMPTY_IMAGE_RESOURCE
                    rotation = -180f

                    animate().alpha(1f)
                            .scaleX(1f)
                            .scaleY(1f)
                            .rotation(0f)
                            .start()
                }
    }

    fun reset() {
        imageResource = EMPTY_IMAGE_RESOURCE
        visibility = View.GONE
        isClickable = false
    }
}