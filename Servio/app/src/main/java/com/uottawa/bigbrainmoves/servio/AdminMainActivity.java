package com.uottawa.bigbrainmoves.servio;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
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

public class AdminMainActivity extends AppCompatActivity {
    private FirebaseDatabase mDatabase = FirebaseDatabase.getInstance();
    private DatabaseReference myRef = mDatabase.getReference(); // gets db ref, then searches for username.

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_main);
        TextView welcomeText = findViewById(R.id.welcomeMessageText);
        CurrentUser currentUser = new CurrentUser();
        //TODO: SET CURRENT ROLE TO ADMIN

        final String text = "Welcome " + currentUser.getCurrentUser().getDisplayName() +
                ", current role is";
        welcomeText.setText(text);

        // TODO Add A list of all users, Please look at recycler view and contact if need help.

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

        myRef.child("user_info").addListenerForSingleValueEvent(new ValueEventListener() {
            //
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // user exists!
                if (dataSnapshot != null) {
                    User user = dataSnapshot.getValue(User.class);
                    CurrentUser currentUser = new CurrentUser();
                    currentUser.setCurrentUser(user);
                }
            }

            @Override // Some kind of error
            public void onCancelled(DatabaseError dbError) {

            }
        });

    }

}
