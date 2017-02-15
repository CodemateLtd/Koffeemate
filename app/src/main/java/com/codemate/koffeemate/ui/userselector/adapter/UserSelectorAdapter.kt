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

import android.support.v7.util.DiffUtil
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.bumptech.glide.Glide
import com.codemate.koffeemate.R
import com.codemate.koffeemate.data.models.User
import kotlinx.android.synthetic.main.recycler_item_user.view.*
import org.jetbrains.anko.onClick

open class UserSelectorAdapter(val onUserSelectedListener: (user: User) -> Unit) :
        RecyclerView.Adapter<UserSelectorAdapter.ViewHolder>() {
    internal var users = emptyList<User>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val itemView = inflater.inflate(R.layout.recycler_item_user, parent, false)

        return ViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val user = users[position]
        holder.bind(user)
    }

    override fun getItemCount() = users.size

    open fun setItems(users: List<User>) {
        val diffResult = DiffUtil.calculateDiff(UserDiffCallback(this.users, users))
        this.users = users
        diffResult.dispatchUpdatesTo(this)
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bind(user: User) = with(itemView) {
            Glide.with(context)
                    .load(user.profile.smallestAvailableImage)
                    .error(R.drawable.ic_user_unknown)
                    .into(profileImage)
            userName.text = user.profile.real_name

            onClick { onUserSelectedListener(user) }
        }
    }

    fun clear() {
        this.users = emptyList()
        notifyDataSetChanged()
    }
}

class UserDiffCallback(val oldUsers: List<User>, val newUsers: List<User>) : DiffUtil.Callback() {
    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int) = oldUsers[oldItemPosition].id == newUsers[newItemPosition].id
    override fun getOldListSize() = oldUsers.size
    override fun getNewListSize() = newUsers.size
    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int) = oldUsers[oldItemPosition] == newUsers[newItemPosition]
}