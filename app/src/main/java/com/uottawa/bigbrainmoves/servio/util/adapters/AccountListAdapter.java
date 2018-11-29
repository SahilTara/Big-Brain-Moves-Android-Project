package com.uottawa.bigbrainmoves.servio.util.adapters;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.uottawa.bigbrainmoves.servio.R;
import com.uottawa.bigbrainmoves.servio.models.Account;

import java.util.ArrayList;
import java.util.List;

public class AccountListAdapter extends RecyclerView.Adapter<AccountListAdapter.ViewHolder> {
    private List<Account> accounts = new ArrayList<>();

    public AccountListAdapter(List<Account> accounts) {
        this.accounts.addAll(accounts);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
         View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.user_row, viewGroup, false);
         return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int position) {
        final Account account = accounts.get(position);
        viewHolder.userText.setText(account.getUsername());
        viewHolder.typeText.setText(account.getType().toString());
    }

    @Override
    public int getItemCount() {
        return accounts.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView userText;
        TextView typeText;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            userText = itemView.findViewById(R.id.userText);
            typeText = itemView.findViewById(R.id.typeText);
        }
    }
}
