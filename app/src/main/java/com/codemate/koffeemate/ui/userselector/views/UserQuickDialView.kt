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

package com.codemate.koffeemate.ui.userselector.views

import android.content.Context
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.AttributeSet
import android.widget.FrameLayout
import com.codemate.koffeemate.R
import com.codemate.koffeemate.data.models.User
import com.codemate.koffeemate.ui.userselector.UserItemAnimator
import com.codemate.koffeemate.ui.userselector.UserSelectListener
import com.codemate.koffeemate.ui.userselector.adapter.UserQuickDialAdapter
import kotlinx.android.synthetic.main.view_user_quick_dial.view.*
import org.jetbrains.anko.backgroundResource
import org.jetbrains.anko.dip

class UserQuickDialView : FrameLayout {
    private val HIDE_DELAY_MS = 10000L

    constructor(ctx: Context) : super(ctx)
    constructor(ctx: Context, attrs: AttributeSet) : super(ctx, attrs)

    var userSelectorAdapter: UserQuickDialAdapter
    var hideOffset = 0f
    var resetRunnable: Runnable? = null
    var userSelectListener: UserSelectListener? = null
    var onMoreClickedListener: (() -> Unit)? = null

    init {
        layoutParams = LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT)
        backgroundResource = R.drawable.background_shadow

        inflate(context, R.layout.view_user_quick_dial, this)

        userSelectorAdapter = UserQuickDialAdapter(
                onUserSelectedListener = {
                    reset()
                    userSelectListener?.onUserSelected(it, UserSelectListener.Companion.REQUEST_WHOS_BREWING)
                },
                onMoreClickedListener = {
                    reset()
                    onMoreClickedListener?.invoke()
                }
        )

        quickDialRecycler.adapter = userSelectorAdapter
        quickDialRecycler.layoutManager = LinearLayoutManager(context, RecyclerView.HORIZONTAL, false)
        quickDialRecycler.itemAnimator = UserItemAnimator()

        hideOffset = dip(200).toFloat()
        translationY = hideOffset
        alpha = 0f
    }

    fun setUsers(users: List<User>, hideListener: () -> Unit) {
        userSelectorAdapter.setItems(users)

        animate().alpha(1f)
                .translationY(0f)
                .start()

        resetRunnable = Runnable {
            reset()
            hideListener()
        }

        handler.postDelayed(resetRunnable, HIDE_DELAY_MS)
    }

    fun reset() {
        handler.removeCallbacks(resetRunnable)
        animate().alpha(0f)
                .translationY(hideOffset)
                .withEndAction {
                    userSelectorAdapter.clear()
                }
                .start()
    }
}