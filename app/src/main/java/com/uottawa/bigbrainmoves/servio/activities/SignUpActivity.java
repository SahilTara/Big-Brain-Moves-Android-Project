package com.uottawa.bigbrainmoves.servio.activities;

import android.content.Intent;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.dd.processbutton.iml.ActionProcessButton;
import com.google.android.material.textfield.TextInputLayout;
import com.jaredrummler.materialspinner.MaterialSpinner;
import com.uottawa.bigbrainmoves.servio.presenters.SignupPresenter;
import com.uottawa.bigbrainmoves.servio.repositories.DbHandler;
import com.uottawa.bigbrainmoves.servio.repositories.Repository;
import com.uottawa.bigbrainmoves.servio.util.CurrentAccount;
import com.uottawa.bigbrainmoves.servio.R;
import com.uottawa.bigbrainmoves.servio.util.UiUtil;
import com.uottawa.bigbrainmoves.servio.models.Account;
import com.uottawa.bigbrainmoves.servio.util.enums.AccountType;
import com.uottawa.bigbrainmoves.servio.views.SignUpView;

public class SignUpActivity extends AppCompatActivity implements SignUpView {

    private AccountType typeSelected;
    private SignupPresenter presenter;
    private final Repository repository = new DbHandler();

    private MaterialSpinner spinner;
    private ActionProcessButton signUpButton;

    private TextInputLayout emailInputLayout;
    private TextInputLayout displayNameInputLayout;
    private TextInputLayout userInputLayout;
    private TextInputLayout passwordInputLayout;

    private EditText userText;
    private EditText emailText;
    private EditText passwordText;
    private EditText displayNameText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        spinner = findViewById(R.id.userTypeSpinner);
        spinner.setItems(AccountType.NONE, AccountType.SERVICE_PROVIDER, AccountType.HOME_OWNER);
        typeSelected = AccountType.NONE;

        signUpButton = findViewById(R.id.btnSignUp);
        signUpButton.setMode(ActionProcessButton.Mode.ENDLESS);

        presenter = new SignupPresenter(this, repository);
        presenter.checkIfAdminExists();

        emailText = findViewById(R.id.emailText);
        emailInputLayout = findViewById(R.id.emailInputLayout);

        displayNameText = findViewById(R.id.displayNameText);
        displayNameInputLayout = findViewById(R.id.displayNameInputLayout);

        userText = findViewById(R.id.userText);
        userInputLayout = findViewById(R.id.usernameInputLayout);

        passwordText = findViewById(R.id.passwordText);
        passwordInputLayout = findViewById(R.id.passwordInputLayout);

        emailText.setOnFocusChangeListener(this::setFocus);
        displayNameText.setOnFocusChangeListener(this::setFocus);
        userText.setOnFocusChangeListener(this::setFocus);
        passwordText.setOnFocusChangeListener(this::setFocus);

        spinner.setOnItemSelectedListener((MaterialSpinner.OnItemSelectedListener<AccountType>) (view, position, id, item) -> {
            typeSelected = item;
            signUpButton.setProgress(0);
        });
    }

    /**
     * Handler method for focus listener, we use this method to reset the message on the button.
     * @param view a view
     * @param hasFocus whether the component has received focus or not.
     */
    private void setFocus(View view, boolean hasFocus) {
        if (hasFocus)
            signUpButton.setProgress(0);
    }

    /**
     * Method sets the enable state of btnSignUp, and the many texts.
     * @param isEnabled specifies whether components should be enabled or disabled.
     */
    private void setEnableStateOfSignUpComponents(boolean isEnabled) {
        signUpButton.setEnabled(isEnabled);
        emailText.setEnabled(isEnabled);
        displayNameText.setEnabled(isEnabled);
        userText.setEnabled(isEnabled);
        passwordText.setEnabled(isEnabled);
    }

    /**
     * Sets all text input layout error messages in current activity
     * to null when this method is called.
     */
    private void resetErrors() {
        emailInputLayout.setError(null);
        displayNameInputLayout.setError(null);
        userInputLayout.setError(null);
        passwordInputLayout.setError(null);
        spinner.setError(null, null);
    }

    public void btnSignUpClick(View view) {
        setEnableStateOfSignUpComponents(false);
        resetErrors();

        signUpButton.setProgress(1);

        String username = userText.getText().toString();
        String email = emailText.getText().toString();
        String password = passwordText.getText().toString();
        String displayName = displayNameText.getText().toString();

        presenter.createAccount(email, username, password, displayName, typeSelected);
    }

    @Override
    public void displayEmailTaken() {
        emailInputLayout.setError("Email is already associated to an account.");
        setEnableStateOfSignUpComponents(true);
        signUpButton.setProgress(-1);
    }

    @Override
    public void displayUserNameTaken() {
        userInputLayout.setError("Username has already been taken.");
        setEnableStateOfSignUpComponents(true);
        signUpButton.setProgress(-1);
    }

    @Override
    public void displaySignUpSuccess() {
        Toast.makeText(getApplicationContext(),
                "Successfully Signed Up!",
                Toast.LENGTH_SHORT).show();
        presenter.attemptGettingAccountInfo();
        signUpButton.setProgress(100);
    }

    @Override
    public void displayInvalidEmail() {
        emailInputLayout.setError("Invalid Email Address");
        setEnableStateOfSignUpComponents(true);
        signUpButton.setProgress(-1);
    }

    @Override
    public void displayInvalidUserNameLength() {
        userInputLayout.setError("Username must have minimum length of 3.");
        setEnableStateOfSignUpComponents(true);
        signUpButton.setProgress(-1);
    }

    @Override
    public void displayInvalidUserNameAlphanumeric() {
        userInputLayout.setError("Username can only contain alphanumeric or .-_ characters.");
        setEnableStateOfSignUpComponents(true);
        signUpButton.setProgress(-1);
    }

    @Override
    public void displayInvalidDisplayName() {
        displayNameInputLayout.setError("Display Name must only contain A-Z and spaces.");
        setEnableStateOfSignUpComponents(true);
        signUpButton.setProgress(-1);
    }

    @Override
    public void displayInvalidPassword() {
        passwordInputLayout.setError("Passwords must be greater than 6" +
                                     "characters long and contain no spaces.");

        setEnableStateOfSignUpComponents(true);
        signUpButton.setProgress(-1);
    }

    @Override
    public void displayInvalidType() {
        Toast.makeText(getApplicationContext(),
                "You must select a valid type.",
                Toast.LENGTH_LONG).show();
        spinner.setError("You must select a valid type.");
        setEnableStateOfSignUpComponents(true);
        signUpButton.setProgress(-1);
    }

    @Override
    public void displayAdminDoesNotExist() {
        spinner.setItems(AccountType.NONE, AccountType.ADMIN,
                AccountType.SERVICE_PROVIDER,
                AccountType.HOME_OWNER);
    }

    @Override
    public void displayNoAccountFound() {
        Toast.makeText(getApplicationContext(),
                "Please contact bigbrainmoves@gmail.com if cannot sign in.",
                Toast.LENGTH_LONG).show();
        setEnableStateOfSignUpComponents(true);
        signUpButton.setProgress(0);
    }

    @Override
    public void displayDataError() {
        Toast.makeText(getApplicationContext(),
                "Insufficient permissions to communicate with database.",
                Toast.LENGTH_LONG).show();
        setEnableStateOfSignUpComponents(true);
        signUpButton.setProgress(-1);
    }

    @Override
    public void displayValidAccount() {
        Toast.makeText(getApplicationContext(),
                "Logged In!",
                Toast.LENGTH_LONG).show();
        Account account = CurrentAccount.getInstance().getCurrentAccount();
        Intent openCorrectUi = UiUtil.getIntentFromType(getApplicationContext(),
                account.getType());
        startActivity(openCorrectUi);
    }
}
