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

package com.codemate.koffeemate.ui.userselector

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.support.v7.widget.DefaultItemAnimator
import android.support.v7.widget.RecyclerView
import android.view.animation.DecelerateInterpolator
import kotlinx.android.synthetic.main.recycler_item_user.view.*

class UserItemAnimator : DefaultItemAnimator() {
    private val interpolator = DecelerateInterpolator(3f)

    override fun animateAdd(viewHolder: RecyclerView.ViewHolder): Boolean {
        if (viewHolder is UserSelectorAdapter.ViewHolder) {
            viewHolder.itemView.userName.alpha = 0f

            viewHolder.itemView.profileImage.alpha = 0.5f
            viewHolder.itemView.profileImage.scaleX = 0f
            viewHolder.itemView.profileImage.scaleY = 0f
            viewHolder.itemView.profileImage.rotation = 180f
            viewHolder.itemView.profileImage.animate()
                    .setInterpolator(interpolator)
                    .setDuration(500)
                    .setStartDelay((250 + viewHolder.layoutPosition * 75).toLong())
                    .setListener(object : AnimatorListenerAdapter() {
                        override fun onAnimationEnd(animation: Animator) {
                            viewHolder.itemView.userName.animate()
                                    .alpha(1f)
                                    .withEndAction { this@UserItemAnimator.dispatchAddFinished(viewHolder) }
                                    .start()
                        }
                    })
                    .alpha(1f)
                    .scaleX(1f)
                    .scaleY(1f)
                    .rotation(0f)
                    .start()
        }

        return true
    }
}