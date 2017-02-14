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

import android.animation.ObjectAnimator
import android.content.Context
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.AttributeSet
import android.view.ViewGroup
import android.view.animation.AccelerateDecelerateInterpolator
import android.widget.FrameLayout
import com.codemate.koffeemate.R
import com.codemate.koffeemate.data.models.User
import com.codemate.koffeemate.ui.userselector.UserItemAnimator
import com.codemate.koffeemate.ui.userselector.UserSelectorAdapter
import kotlinx.android.synthetic.main.recycler_item_user.view.*
import kotlinx.android.synthetic.main.view_user_quick_dial.view.*
import org.jetbrains.anko.backgroundResource

class UserQuickDialView : FrameLayout {
    constructor(ctx: Context) : super(ctx)
    constructor(ctx: Context, attrs: AttributeSet) : super(ctx, attrs)

    var userSelectorAdapter: UserQuickDialAdapter

    init {
        layoutParams = LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT)
        backgroundResource = R.drawable.background_shadow
        alpha = 0.2f

        inflate(context, R.layout.view_user_quick_dial, this)

        userSelectorAdapter = UserQuickDialAdapter {}
        quickDialRecycler.adapter = userSelectorAdapter
        quickDialRecycler.layoutManager = LinearLayoutManager(context, RecyclerView.HORIZONTAL, false)
        quickDialRecycler.itemAnimator = UserItemAnimator()
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        translationY = height.toFloat()
    }

    fun setUsers(users: List<User>) {
        animate().setDuration(300)
                .alpha(1f)
                .translationY(0f)
                .withEndAction { userSelectorAdapter.setItems(users) }
                .start()

        postDelayed({
            animate().setDuration(300)
                    .translationY(height.toFloat())
                    .alpha(0f)
                    .start()
        }, 3000)
    }
}

class UserQuickDialAdapter(onUserSelectedListener: (User) -> Unit)
    : UserSelectorAdapter(onUserSelectedListener) {
    val accelerateDecelerateInterpolator = AccelerateDecelerateInterpolator()

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        super.onBindViewHolder(holder, position)
        holder.itemView.userName.text = users[position].profile.first_name

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
