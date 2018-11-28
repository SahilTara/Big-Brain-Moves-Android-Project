package com.uottawa.bigbrainmoves.servio.activities;

import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.dd.processbutton.iml.ActionProcessButton;
import com.google.android.material.textfield.TextInputLayout;
import com.uottawa.bigbrainmoves.servio.R;
import com.uottawa.bigbrainmoves.servio.models.Account;
import com.uottawa.bigbrainmoves.servio.presenters.LoginOrSignUpPresenter;
import com.uottawa.bigbrainmoves.servio.repositories.DbHandler;
import com.uottawa.bigbrainmoves.servio.repositories.Repository;
import com.uottawa.bigbrainmoves.servio.util.CurrentAccount;
import com.uottawa.bigbrainmoves.servio.util.UiUtil;
import com.uottawa.bigbrainmoves.servio.views.LoginOrSignUpView;

public class LoginOrSignUpActivity extends AppCompatActivity implements LoginOrSignUpView {
    private final Repository repository = new DbHandler();
    private LoginOrSignUpPresenter presenter;
    private ActionProcessButton btnSignIn;
    private EditText usernameText;
    private EditText passwordText;

    private TextInputLayout usernameInputLayout;
    private TextInputLayout passwordInputLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_or_signup);

        presenter = new LoginOrSignUpPresenter(this, repository);
        btnSignIn = findViewById(R.id.btnSignIn);

        usernameText = findViewById(R.id.userText);
        passwordText = findViewById(R.id.passwordText);

        usernameInputLayout = findViewById(R.id.usernameInputLayout);
        passwordInputLayout = findViewById(R.id.passwordInputLayout);

        btnSignIn.setMode(ActionProcessButton.Mode.ENDLESS);

        usernameText.setOnFocusChangeListener((v, hasFocus) -> {
            if (hasFocus)
                btnSignIn.setProgress(0);

        });

        passwordText.setOnFocusChangeListener((view, hasFocus) -> {
            if (hasFocus)
                btnSignIn.setProgress(0);
        });

    }


    public void btnSignInClick(View view) {
        setEnableStateOfLoginComponents(false);
        btnSignIn.setProgress(1);

        usernameInputLayout.setError(null);
        passwordInputLayout.setError(null);

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
        btnSignIn.setProgress(100);
    }

    @Override
    public void displayInvalidLogin() {
        Toast.makeText(getApplicationContext(), "Invalid Credentials", Toast.LENGTH_LONG).show();
        btnSignIn.setProgress(-1);
        setEnableStateOfLoginComponents(true);
    }

    @Override
    public void displayInvalidUser() {
        usernameInputLayout.setError("A username/email must be entered!");
        usernameText.setError(null);
        btnSignIn.setProgress(-1);
        setEnableStateOfLoginComponents(true);
    }

    @Override
    public void displayInvalidPassword() {
        passwordInputLayout.setError("A password must be entered!");
        passwordText.setError(null);
        btnSignIn.setProgress(-1);
        setEnableStateOfLoginComponents(true);
    }

    @Override
    public void displayNoAccountFound() {
        Toast.makeText(getApplicationContext(),
                "Please contact bigbrainmoves@gmail.com, your account's data seems to be corrupted",
                Toast.LENGTH_LONG).show();
        btnSignIn.setProgress(-1);
        setEnableStateOfLoginComponents(true);
    }

    @Override
    public void displayDataError() {
        Toast.makeText(getApplicationContext(),
                "Insufficient permissions to communicate with database.",
                Toast.LENGTH_LONG).show();
        btnSignIn.setProgress(-1);
        setEnableStateOfLoginComponents(true);
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

    /**
     * Method sets the enable state of btnSignIn, and the username + password texts.
     * @param isEnabled specifies whether components should be enabled or disabled.
     */
    private void setEnableStateOfLoginComponents(boolean isEnabled) {
        btnSignIn.setEnabled(isEnabled);
        usernameText.setEnabled(isEnabled);
        passwordText.setEnabled(isEnabled);
    }
}
