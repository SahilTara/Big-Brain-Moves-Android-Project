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
