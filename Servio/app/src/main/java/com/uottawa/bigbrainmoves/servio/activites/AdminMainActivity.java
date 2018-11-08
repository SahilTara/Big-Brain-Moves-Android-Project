package com.uottawa.bigbrainmoves.servio.activites;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.uottawa.bigbrainmoves.servio.util.AccountListAdapter;
import com.uottawa.bigbrainmoves.servio.util.CurrentAccount;
import com.uottawa.bigbrainmoves.servio.R;
import com.uottawa.bigbrainmoves.servio.models.Account;

import java.util.ArrayList;

public class AdminMainActivity extends AppCompatActivity {
    private FirebaseDatabase mDatabase = FirebaseDatabase.getInstance();
    private DatabaseReference myRef = mDatabase.getReference(); // gets db ref, then searches for username.
    private  CurrentAccount currentAccount = CurrentAccount.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_main);
        TextView welcomeText = findViewById(R.id.welcomeMessageText);


        final String text = "Welcome " + currentAccount.getCurrentAccount().getDisplayName() +
                ", current role is Admin";
        welcomeText.setText(text);
    }

    public void onSignoutClick(View view) {
        FirebaseAuth.getInstance().signOut();
        currentAccount.setCurrentAccount(null);
        Intent intent = new Intent(getApplicationContext(), LoginOrSignUpActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);

    }

    public void onUserListClick(View view) {
        Intent intent = new Intent(getApplicationContext(), UserListActivity.class);
        startActivity(intent);
    }




}
