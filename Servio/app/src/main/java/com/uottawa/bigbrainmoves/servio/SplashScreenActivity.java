package com.uottawa.bigbrainmoves.servio;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class SplashScreenActivity extends AppCompatActivity {

    private FirebaseAuth mFirebaseAuth;
    private FirebaseAuth.AuthStateListener mFireBaseAuthStateListener;
    private FirebaseDatabase mDatabase = FirebaseDatabase.getInstance();
    private DatabaseReference myRef = mDatabase.getReference(); // gets db ref, then searches for username.
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

        // We don't use db handler for handling the db for the most part since we want to only use
        // the listener here.

        // get the firebaseAuth instance
        mFirebaseAuth = FirebaseAuth.getInstance();
        FirebaseUser user = mFirebaseAuth.getCurrentUser();
        if (user != null) {
            getUserFromDataBase(user.getUid());
        }
        // create an auth listener, useful for making sure user is signed out before showing login.
        mFireBaseAuthStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser firebaseUser = mFirebaseAuth.getCurrentUser();
                if (firebaseUser != null) {
                    // user is signed in already!

                    getUserFromDataBase(firebaseUser.getUid());

                } else {
                    // signed out so send them to the login/signup page.
                    notLoggedIn();

                }
            }
        };
    }

    @Override
    protected void onPause() {
        super.onPause();
        mFirebaseAuth.removeAuthStateListener(mFireBaseAuthStateListener);
    }

    @Override
    protected void onResume() {
        super.onResume();
        mFirebaseAuth.addAuthStateListener(mFireBaseAuthStateListener);
    }

    private void getUserFromDataBase(String uid) {

        myRef.child("user_info").child(uid)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    //
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        //TODO: ADD A TOAST SAYING THEY LOGGED IN
                        if (dataSnapshot != null) {
                            User user = dataSnapshot.getValue(User.class);
                            if (user == null) {
                                notLoggedIn();
                                return;
                            }
                            CurrentUser currentUser = new CurrentUser();
                            currentUser.setCurrentUser(user);
                            Toast.makeText(getApplicationContext(),"Logged In", Toast.LENGTH_LONG).show();
                            Intent openCorrectUi = UiUtil.getIntentFromType(getApplicationContext(),
                                    user.getType());
                            startActivity(openCorrectUi);
                        }
                    }

                    @Override // Some kind of error
                    public void onCancelled(DatabaseError dbError) {

                    }
                });

    }

    private void notLoggedIn() {
        Intent signupOrLogin = new Intent(getApplicationContext(), LoginOrSignUpActivity.class);
        startActivity(signupOrLogin);
    }
}
