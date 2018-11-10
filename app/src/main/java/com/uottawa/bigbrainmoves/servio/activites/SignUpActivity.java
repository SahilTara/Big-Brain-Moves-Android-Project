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
import com.jaredrummler.materialspinner.MaterialSpinner;
import com.uottawa.bigbrainmoves.servio.presenters.SignupPresenter;
import com.uottawa.bigbrainmoves.servio.repositories.DbHandler;
import com.uottawa.bigbrainmoves.servio.repositories.Repository;
import com.uottawa.bigbrainmoves.servio.util.CurrentAccount;
import com.uottawa.bigbrainmoves.servio.R;
import com.uottawa.bigbrainmoves.servio.util.UiUtil;
import com.uottawa.bigbrainmoves.servio.models.Account;
import com.uottawa.bigbrainmoves.servio.views.SignUpView;

public class SignUpActivity extends AppCompatActivity implements SignUpView {

    private MaterialSpinner spinner;
    private String typeSelected;
    private SignupPresenter presenter;
    private Repository repository = new DbHandler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
        spinner = findViewById(R.id.userTypeSpinner);
        spinner.setItems("", "service", "home");
        typeSelected = "";
        presenter = new SignupPresenter(this, repository);
        presenter.checkIfAdminExists();

        spinner.setOnItemSelectedListener(new MaterialSpinner.OnItemSelectedListener<String>() {

            @Override public void onItemSelected(MaterialSpinner view, int position, long id, String item) {
                typeSelected = item;
            }
        });
    }

    public void btnSignUpClick(View view) {


        EditText userText = findViewById(R.id.userText);
        EditText emailText = findViewById(R.id.emailText);
        EditText passwordText = findViewById(R.id.passwordText);
        EditText displayNameText = findViewById(R.id.displayNameText);

        String username = userText.getText().toString();
        String email = emailText.getText().toString();
        String password = passwordText.getText().toString();
        String displayName = displayNameText.getText().toString();

        presenter.createAccount(email, username, password, displayName, typeSelected);
    }

    @Override
    public void displayEmailTaken() {
        Toast.makeText(getApplicationContext(),
                "Email is already associated to an account!",
                Toast.LENGTH_LONG).show();
    }

    @Override
    public void displayUserNameTaken() {
        Toast.makeText(getApplicationContext(),
                "Username is already taken!",
                Toast.LENGTH_LONG).show();
    }

    @Override
    public void displaySignUpSuccess() {
        Toast.makeText(getApplicationContext(),
                "Successfully Signed Up!",
                Toast.LENGTH_SHORT).show();
        presenter.attemptGettingAccountInfo();
    }

    @Override
    public void displayInvalidEmail() {
        Toast.makeText(getApplicationContext(),
                "Invalid Email Address",
                Toast.LENGTH_LONG).show();
    }

    @Override
    public void displayInvalidUserName() {
        Toast.makeText(getApplicationContext(),
                "Username must have length >= 3, Alphanumeric or .-_",
                Toast.LENGTH_LONG).show();
    }

    @Override
    public void displayInvalidDisplayName() {
        Toast.makeText(getApplicationContext(),
                "DisplayName must contain only A-Z and Spaces.",
                Toast.LENGTH_LONG).show();
    }

    @Override
    public void displayInvalidPassword() {
        Toast.makeText(getApplicationContext(),
                "Password must have length >= 6 and contain no spaces",
                Toast.LENGTH_LONG).show();
    }

    @Override
    public void displayInvalidType() {
        Toast.makeText(getApplicationContext(),
                "You must select a valid type.",
                Toast.LENGTH_LONG).show();
    }

    @Override
    public void displayAdminDoesNotExist() {
        spinner.setItems("", "admin", "user", "service");
    }

    @Override
    public void displayNoAccountFound() {
        Toast.makeText(getApplicationContext(),
                "Please contact bigbrainmoves@gmail.com if cannot sign in.",
                Toast.LENGTH_LONG).show();
    }

    @Override
    public void displayDataError() {
        Toast.makeText(getApplicationContext(),
                "Insufficient permissions to communicate with database.",
                Toast.LENGTH_LONG).show();
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
