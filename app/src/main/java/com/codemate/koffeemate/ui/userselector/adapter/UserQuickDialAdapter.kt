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

package com.codemate.koffeemate.ui.userselector.adapter

import android.animation.ObjectAnimator
import android.view.animation.AccelerateDecelerateInterpolator
import com.codemate.koffeemate.R
import com.codemate.koffeemate.data.models.User
import kotlinx.android.synthetic.main.recycler_item_user.view.*
import org.jetbrains.anko.imageResource
import org.jetbrains.anko.onClick

class UserQuickDialAdapter(
        onUserSelectedListener: (User) -> Unit,
        private val onMoreClickedListener: () -> Unit) : UserSelectorAdapter(onUserSelectedListener) {
    val accelerateDecelerateInterpolator = AccelerateDecelerateInterpolator()

    private val TYPE_USER = 1
    private val TYPE_MORE = 2

    override fun getItemCount() =
            if (users.isEmpty()) 0
            else users.size + 1

    override fun getItemViewType(position: Int) =
            if (position < users.size)
                TYPE_USER
            else
                TYPE_MORE

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        when (getItemViewType(position)) {
            TYPE_USER -> {
                super.onBindViewHolder(holder, position)
                holder.itemView.userName.text = users[position].profile.first_name

            }
            TYPE_MORE -> {
                holder.itemView.profileImage.imageResource = R.drawable.ic_more
                holder.itemView.userName.text = holder.itemView.context.getString(R.string.more)
                holder.itemView.onClick { onMoreClickedListener.invoke() }
            }
        }

        with(ObjectAnimator.ofFloat(1f, 0f)) {
            interpolator = accelerateDecelerateInterpolator
            duration = 750
            repeatMode = ObjectAnimator.REVERSE
            repeatCount = ObjectAnimator.INFINITE
            startDelay = (position * 250).toLong()

            addUpdateListener {
                val value = animatedValue as Float

                holder.itemView.apply {
                    translationY = -value * 10
                }
            }

            start()
        }
    }
}