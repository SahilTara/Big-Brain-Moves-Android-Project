package com.uottawa.bigbrainmoves.servio;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.TaskCompletionSource;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.jaredrummler.materialspinner.MaterialSpinner;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.regex.Pattern;

public class SignUpActivity extends AppCompatActivity {

    MaterialSpinner spinner;
    String typeSelected = null;
    private FirebaseDatabase mDatabase = FirebaseDatabase.getInstance();
    private DatabaseReference myRef = mDatabase.getReference(); // gets db ref, then searches for username.
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
        spinner = findViewById(R.id.userTypeSpinner);
        spinner.setItems("service", "home");
        doesAdminAccountExist();
        spinner.setOnItemSelectedListener(new MaterialSpinner.OnItemSelectedListener<String>() {

            @Override public void onItemSelected(MaterialSpinner view, int position, long id, String item) {
                typeSelected = item;
            }
        });
    }

    public void btnSignUpClick(View view) {
        if (typeSelected == null) {
            Toast.makeText(getApplicationContext(),
                    "You must select a type first!",
                    Toast.LENGTH_LONG).show();
            return;
        }

        EditText userText = findViewById(R.id.userText);
        EditText emailText = findViewById(R.id.emailText);
        EditText passwordText = findViewById(R.id.passwordText);
        EditText displayNameText = findViewById(R.id.displayNameText);

        String username = userText.getText().toString();
        String email = emailText.getText().toString();
        String password = passwordText.getText().toString();
        String displayName = displayNameText.getText().toString();

        createUserIfNotInDataBase(email, username, password, displayName, typeSelected);
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
                            User user = new User(displayName, type, username);
                            writeUserNameToDatabase(username, email);
                            writeUserToDataBase(firebaseUser.getUid(), user);
                            CurrentUser currentUser = new CurrentUser();
                            currentUser.setCurrentUser(user);

                            Toast.makeText(getApplicationContext(), "Account created", Toast.LENGTH_LONG).show();

                            Intent openCorrectUi = UiUtil.getIntentFromType(getApplicationContext(),
                                    user.getType());
                            startActivity(openCorrectUi);
                        } else {
                            Toast.makeText(getApplicationContext(), "Email is taken!", Toast.LENGTH_LONG).show();
                        }
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
     * Method to check if an admin account exists.
     * @return the existence of an admin account.
     */
    private void doesAdminAccountExist() {


        myRef.child("admin_exists").addListenerForSingleValueEvent(new ValueEventListener() {

            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // admin exists!
                if (dataSnapshot != null) {
                    Boolean bool = dataSnapshot.getValue(Boolean.class);
                    if (bool == null || !bool)
                        spinner.setItems("admin", "user", "service");
                }
            }

            @Override // Some kind of error
            public void onCancelled(DatabaseError dbError) {

            }
        });
    }
    /**
     * Checks if the user exists, if not adds to db.
     * @param username the username to check.
     * @param email the email of user
     * @param
     */
    private void createUserIfNotInDataBase(final String email, final String username, final String password,
                                           final String displayName, final String typeSelected) {
        // We make sure that a user does not exist with this uid.
        myRef.child("user_ids").child(username)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    //
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        // user already exists
                        if (!dataSnapshot.exists()) {
                            if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                                Toast.makeText(getApplicationContext(),
                                        "Invalid Email Address",
                                        Toast.LENGTH_LONG).show();
                                return;
                            } else if (!username.matches("\\b[a-zA-Z][a-zA-Z0-9\\-._]{3,}\\b")) {
                                Toast.makeText(getApplicationContext(),
                                        "Username must have length >= 3, Alphanumeric or .-_",
                                        Toast.LENGTH_LONG).show();
                                return;
                            } else if (!displayName.matches("(([A-Za-z]+\\s?)+)")) {
                                Toast.makeText(getApplicationContext(),
                                        "DisplayName must contain only A-Z and Spaces.",
                                        Toast.LENGTH_LONG).show();
                                return;
                            } else if (!password.matches("^(?=\\S+$).{6,}$")) {
                                Toast.makeText(getApplicationContext(),
                                        "Password must have length >= 6",
                                        Toast.LENGTH_LONG).show();
                                return;
                            }
                            // user does not exist
                            createUser(email, username, password, displayName, typeSelected);

                        }  else {
                            // TODO: Make Toast saying username taken.
                            Toast.makeText(getApplicationContext(), "User taken", Toast.LENGTH_LONG).show();
                        }
                    }

                    @Override // Some kind of error
                    public void onCancelled(DatabaseError dbError) {

                    }
                });

    }
}
