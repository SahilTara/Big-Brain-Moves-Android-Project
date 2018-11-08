package com.uottawa.bigbrainmoves.servio.activites;

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
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.uottawa.bigbrainmoves.servio.presenters.LoginOrSignUpPresenter;
import com.uottawa.bigbrainmoves.servio.repositories.DbHandler;
import com.uottawa.bigbrainmoves.servio.repositories.Repository;
import com.uottawa.bigbrainmoves.servio.util.CurrentAccount;
import com.uottawa.bigbrainmoves.servio.R;
import com.uottawa.bigbrainmoves.servio.util.UiUtil;
import com.uottawa.bigbrainmoves.servio.models.Account;
import com.uottawa.bigbrainmoves.servio.views.LoginOrSignUpView;

public class LoginOrSignUpActivity extends AppCompatActivity implements LoginOrSignUpView {
    private Repository repository = new DbHandler();
    private LoginOrSignUpPresenter presenter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_or_signup);
        presenter = new LoginOrSignUpPresenter(this, repository);
    }


    public void btnSignInClick(View view) {
        EditText usernameText = findViewById(R.id.userText);
        EditText passwordText = findViewById(R.id.passwordText);

        String user = usernameText.getText().toString();
        String password = passwordText.getText().toString();

        presenter.login(user, password);
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
    /*
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
*/

    /**
     * Attempts to verify user validity, and logs in if valid.
     *
     * @param input    The email or username of the user
     * @param password the password of the user
     */
    /*
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
                                Toast.makeText(getApplicationContext(), "Sign In Successful", Toast.LENGTH_LONG).show();
                                FirebaseUser user = mAuth.getCurrentUser();
                                getUserFromDataBase(user.getUid());

                        }  else { // otherwise sign in failed.
                            Toast.makeText(getApplicationContext(), "Sign In Failed", Toast.LENGTH_LONG).show();
                        }
                    }
                });
    }
*/
    /**
     * Gets a Account Object from a firebase id.
     * @param uid the uid of the firebase user
     * @return the Account object corresponding to the firebase id.
     */
    /*
    private void getUserFromDataBase(String uid) {

        myRef.child("user_info").child(uid)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    //
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        // user exists!
                        if (dataSnapshot != null) {
                            Account account = dataSnapshot.getValue(Account.class);
                            CurrentAccount currentAccount = CurrentAccount.getInstance();
                            currentAccount.setCurrentAccount(account);

                            Intent openCorrectUi = UiUtil.getIntentFromType(getApplicationContext(),
                                    currentAccount.getCurrentAccount().getType());

                            startActivity(openCorrectUi);
                        }
                    }

                    @Override // Some kind of error
                    public void onCancelled(DatabaseError dbError) {

                    }
                });

    }
*/

    @Override
    public void displayValidLogin() {
        Toast.makeText(getApplicationContext(), "Valid Credentials", Toast.LENGTH_SHORT).show();
        presenter.attemptGettingAccountInfo();
    }

    @Override
    public void displayInvalidLogin() {
        Toast.makeText(getApplicationContext(), "Invalid Credentials", Toast.LENGTH_LONG).show();
    }

    @Override
    public void displayNoAccountFound() {
        Toast.makeText(getApplicationContext(),
                "Please contact bigbrainmoves@gmail.com, your account's data seems to be corrupted",
                Toast.LENGTH_LONG).show();
    }

    @Override
    public void displayDataError() {
        Toast.makeText(getApplicationContext(),
                "Insufficient permissions to communicate with database.",
                Toast.LENGTH_LONG).show();
    }

    /**
     * Method to be called when the account is found and valid.
     */
    @Override
    public void displayValidAccount() {
        Account account = CurrentAccount.getInstance().getCurrentAccount();
        // Account was not null to get here so we can assume that account will be non null.
        Toast.makeText(getApplicationContext(),"Logged In!", Toast.LENGTH_LONG).show();

        Intent openCorrectUi = UiUtil.getIntentFromType(getApplicationContext(),
                account.getType());
        startActivity(openCorrectUi);
    }
}
