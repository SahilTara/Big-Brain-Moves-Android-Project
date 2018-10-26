package com.uottawa.bigbrainmoves.servio;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.firebase.FirebaseOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.FileInputStream;
import java.lang.reflect.Array;
import java.util.ArrayList;

public class AdminMainActivity extends AppCompatActivity {
    private FirebaseDatabase mDatabase = FirebaseDatabase.getInstance();
    private DatabaseReference myRef = mDatabase.getReference(); // gets db ref, then searches for username.

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_main);
        TextView welcomeText = findViewById(R.id.welcomeMessageText);
        CurrentUser currentUser = new CurrentUser();

        final String text = "Welcome " + currentUser.getCurrentUser().getDisplayName() +
                ", current role is Admin";
        welcomeText.setText(text);

        putAllUsersInList();
    }

    public void onSignoutClick(View view) {
        FirebaseAuth.getInstance().signOut();
        CurrentUser user = new CurrentUser();
        user.setCurrentUser(null);
        Intent intent = new Intent(getApplicationContext(), LoginOrSignUpActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);

    }
    private void putAllUsersInList() {
        final ArrayList<User> users = new ArrayList<>();
        final RecyclerView recyclerView = findViewById(R.id.userListRecycler);
        final RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);

        myRef.child("user_info").addListenerForSingleValueEvent(new ValueEventListener() {
            //
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // user exists!
                if (dataSnapshot != null) {
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        User user = snapshot.getValue(User.class);
                        users.add(user);
                    }
                    recyclerView.setHasFixedSize(true);
                    RecyclerView.Adapter adapter  = new ArrayRecyclerAdapter(users);
                    recyclerView.setAdapter(adapter);
                    recyclerView.setLayoutManager(layoutManager);

                }
            }

            @Override // Some kind of error
            public void onCancelled(DatabaseError dbError) {

            }
        });

    }

}
