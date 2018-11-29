package com.uottawa.bigbrainmoves.servio.activities;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.widget.Toast;

import com.uottawa.bigbrainmoves.servio.R;
import com.uottawa.bigbrainmoves.servio.models.Account;
import com.uottawa.bigbrainmoves.servio.presenters.UserListPresenter;
import com.uottawa.bigbrainmoves.servio.repositories.DbHandler;
import com.uottawa.bigbrainmoves.servio.repositories.Repository;
import com.uottawa.bigbrainmoves.servio.util.adapters.AccountListAdapter;
import com.uottawa.bigbrainmoves.servio.views.UserListView;

import java.util.List;

public class UserListActivity extends AppCompatActivity implements UserListView {
    private Repository repository = new DbHandler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_list);
        UserListPresenter presenter = new UserListPresenter(this, repository);
        presenter.showUserList();
    }

    public void displayDataError() {
        Toast.makeText(getApplicationContext(),
                "Unable to retrieve accounts list due to insufficient permissions.",
                Toast.LENGTH_LONG).show();
    }

    public void displayUsers(List<Account> accounts) {
        RecyclerView recyclerView = findViewById(R.id.userRecyclerView);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setHasFixedSize(true);
        RecyclerView.Adapter adapter  = new AccountListAdapter(accounts);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(layoutManager);
    }
}
