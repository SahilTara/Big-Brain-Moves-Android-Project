package com.uottawa.bigbrainmoves.servio.activites;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.uottawa.bigbrainmoves.servio.presenters.MainScreenPresenter;
import com.uottawa.bigbrainmoves.servio.repositories.DbHandler;
import com.uottawa.bigbrainmoves.servio.repositories.Repository;
import com.uottawa.bigbrainmoves.servio.util.AccountListAdapter;
import com.uottawa.bigbrainmoves.servio.util.CurrentAccount;
import com.uottawa.bigbrainmoves.servio.R;
import com.uottawa.bigbrainmoves.servio.models.Account;
import com.uottawa.bigbrainmoves.servio.views.MainView;

import java.util.ArrayList;

public class AdminMainActivity extends AppCompatActivity implements MainView {
    MainScreenPresenter presenter;
    Repository repository = new DbHandler();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_main);
        presenter = new MainScreenPresenter(this, repository);
        presenter.showWelcomeMessage();
    }

    public void onSignOutClick(View view) {
        presenter.signOut();
    }

    public void onUserListClick(View view) {
        Intent intent = new Intent(getApplicationContext(), UserListActivity.class);
        startActivity(intent);
    }

    public void onManageServiceTypesClick(View view) {
        Intent intent = new Intent(getApplicationContext(), ManageServiceTypesActivity.class);
        startActivity(intent);
    }

    @Override
    public void displaySignOut() {
        Toast.makeText(getApplicationContext(),
                       "Successfully signed out.",
                       Toast.LENGTH_LONG).show();
        Intent intent = new Intent(getApplicationContext(), LoginOrSignUpActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }

    @Override
    public void displayWelcomeText(String displayName) {
        TextView welcomeText = findViewById(R.id.welcomeMessageText);
        final String text = "Welcome " + displayName +
                ", current role is Admin";
        welcomeText.setText(text);
    }
}
