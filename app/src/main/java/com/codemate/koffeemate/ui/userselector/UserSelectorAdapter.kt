package com.codemate.koffeemate.ui.userselector

import android.support.v7.util.DiffUtil
import android.support.v7.widget.RecyclerView
import android.util.Log
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