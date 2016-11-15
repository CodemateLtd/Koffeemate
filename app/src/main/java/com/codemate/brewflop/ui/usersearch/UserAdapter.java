package com.codemate.brewflop.ui.usersearch;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.codemate.brewflop.R;
import com.codemate.brewflop.data.network.model.User;

import java.util.ArrayList;
import java.util.List;

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.ViewHolder> {
    private List<User> users = new ArrayList<>();

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View itemView = inflater.inflate(R.layout.recycler_item_user, parent, false);

        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        User user = users.get(position);
        holder.userName.setText(user.profile.realName);
    }

    @Override
    public int getItemCount() {
        return users.size();
    }

    public void setItems(List<User> users) {
        this.users = users;
        notifyDataSetChanged();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView userName;

        public ViewHolder(View itemView) {
            super(itemView);

            userName = (TextView) itemView.findViewById(R.id.userName);
        }
    }
}
