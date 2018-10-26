package com.uottawa.bigbrainmoves.servio;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.EditText;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class LoginOrSignUpActivity extends AppCompatActivity {
    private FirebaseDatabase mDatabase = FirebaseDatabase.getInstance();
    private DatabaseReference myRef = mDatabase.getReference(); // gets db ref, then searches for username.
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_or_signup);
    }


    public void btnSignInClick(View view) {
        EditText usernameText = findViewById(R.id.userText);
        EditText passwordText = findViewById(R.id.passwordText);

        String user = usernameText.getText().toString();
        String password = passwordText.getText().toString();

        login(user, password);
    }

    public void btnSignUpClick(View view) {
        Intent signUpActivity = new Intent(getApplicationContext(), SignUpActivity.class);
        startActivity(signUpActivity);
    }


    /**
     * logs in by getting email first then passes password to main login.
     *
     * @param username username to login with
     *
     */
    private void loginWithUsername(final String username, final String password) {

        myRef.child("user_ids").child(username)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    //
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        // username exists!
                        if (dataSnapshot != null) {
                            String emailId = dataSnapshot.getValue(String.class);
                            if (emailId ==null) return;
                            login(emailId, password);
                        } else {

                        }
                    }

                    @Override // Some kind of error
                    public void onCancelled(DatabaseError dbError) {

                    }
                });

    }


    /**
     * Attempts to verify user validity, and logs in if valid.
     *
     * @param input    The email or username of the user
     * @param password the password of the user
     */
    public void login(String input, String password) {

        // Check if the input isn't an email, and if it isn't we get the email from the username.
        if (!Patterns.EMAIL_ADDRESS.matcher(input).matches()) {

            loginWithUsername(input, password);
            return;
        }

        // attempt to sign in with the email and password.
        mAuth.signInWithEmailAndPassword(input, password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            // TODO: Add .show() to the end of the toast
                                Toast.make(getApplicationContext(), "Sign In Successful", Toast.LONG_LENGTH).show();
                                FirebaseUser user = mAuth.getCurrentUser();
                                getUserFromDataBase(user.getUid());

                        }  else { // otherwise sign in failed.
                            //TODO: Add .show() to the end of the toast
                        Toast.make(getApplicationContext(), "Sign In Failed", Toast.LONG_LENGTH);
                        }
                    }
                });
    }

    /**
     * Gets a User Object from a firebase id.
     * @param uid the uid of the firebase user
     * @return the User object corresponding to the firebase id.
     */
    private void getUserFromDataBase(String uid) {

        myRef.child("user_info").child(uid)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    //
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        // user exists!
                        if (dataSnapshot != null) {
                            User user = dataSnapshot.getValue(User.class);
                            CurrentUser currentUser = new CurrentUser();
                            currentUser.setCurrentUser(user);

                            Intent openCorrectUi = UiUtil.getIntentFromType(getApplicationContext(),
                                    currentUser.getCurrentUser().getType());

                            startActivity(openCorrectUi);
                        }
                    }

                    @Override // Some kind of error
                    public void onCancelled(DatabaseError dbError) {

                    }
                });

    }


}
