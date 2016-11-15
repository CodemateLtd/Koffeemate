package com.codemate.brewflop.ui.usersearch

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.bumptech.glide.Glide
import com.codemate.brewflop.R
import com.codemate.brewflop.data.network.model.User
import kotlinx.android.synthetic.main.recycler_item_user.view.*
import java.util.*

class UserSearchAdapter : RecyclerView.Adapter<UserSearchAdapter.ViewHolder>() {
    private var users: List<User> = ArrayList()

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

    fun setItems(users: List<User>) {
        this.users = users
        notifyDataSetChanged()
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bind(user: User) = with(itemView) {
            Glide.with(context)
                    .load(user.profile.image72)
                    .into(profileImage)
            userName.text = user.profile.realName
        }
    }
}
