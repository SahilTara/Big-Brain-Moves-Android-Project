package com.uottawa.bigbrainmoves.servio;

import android.support.annotation.NonNull;
import android.util.Log;
import android.util.Patterns;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.TaskCompletionSource;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GetTokenResult;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

public class FirebaseDbHandler implements DbHandler {
    private FirebaseDatabase mDatabase = FirebaseDatabase.getInstance();
    private DatabaseReference myRef = mDatabase.getReference(); // gets db ref, then searches for username.
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
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
        String email;

        // Check if the input isn't an email, and if it isn't we get the email from the username.
        if (!Patterns.EMAIL_ADDRESS.matcher(input).matches()) {

            loginWithUsername(input, password);

        }

        // attempt to sign in with the email and password.
        mAuth.signInWithEmailAndPassword(input, password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information


                        } // otherwise sign in failed.
                    }
                });
        }

    /**
     * Gets the signed in user, from the database.
     * @return The currently signed in user.
     */
    public void getSignedInUser() {

        FirebaseUser firebaseUser = mAuth.getCurrentUser();

        if (firebaseUser != null) {
            // tries to get the user from the db.
            getUserFromDataBase(firebaseUser.getUid());

        }

    }


    /**
     * Attempts to create a firebase user,
     * @param email
     * @param password
     * @param displayName
     * @param type
     * @return
     */
    public void createUser(final String email, final String username, final String password, final String displayName, final String type) {
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            FirebaseUser firebaseUser = mAuth.getCurrentUser();
                            User user = new User(displayName, type);
                            writeUserNameToDatabase(username, email);
                            writeUserToDataBase(firebaseUser.getUid(), user);
                        } // otherwise sign in not successful
                    }
                });

    }

    /**
     * Adds the displayName, type to the database under the uid.
     *
     */
    private void writeUserToDataBase(String uid, User user) {
        myRef.child("user_info").child(uid).setValue(user);
        if (user.type.equals("admin")) {
            // If we write admin type to database we have to do some things to make sure no more admins are made.
            myRef.child("admin_exists").setValue(true);
        }
    }

    /**
     * Writes the username : email to database for username login.
     * @param username username specified by the user.
     * @param email email specified by the user.
     */
    private void writeUserNameToDatabase(String username, String email) {
        myRef.child("user_ids").child(username).setValue(email);
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
                        }
                    }

                    @Override // Some kind of error
                    public void onCancelled(DatabaseError dbError) {

                    }
                });

    }


}


