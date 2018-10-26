package com.uottawa.bigbrainmoves.servio;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

public class ArrayRecyclerAdapter extends RecyclerView.Adapter<ArrayRecyclerAdapter.ViewHolder> {
    ArrayList<User> users = new ArrayList<>();

    public ArrayRecyclerAdapter(ArrayList<User> users) {
        this.users.addAll(users);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
         View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.user_row, viewGroup, false);
         return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int position) {
        final User user = users.get(position);
        final String text = "User: " + user.getUsername() + " Type: " + user.getType();
        viewHolder.userTypeText.setText(text);
    }

    @Override
    public int getItemCount() {
        return users.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView userTypeText;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            userTypeText = itemView.findViewById(R.id.userAndTypeText);
        }
    }
}
