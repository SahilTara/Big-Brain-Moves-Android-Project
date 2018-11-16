package com.uottawa.bigbrainmoves.servio.activities;

import android.content.Intent;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Toast;

import com.uottawa.bigbrainmoves.servio.models.Account;
import com.uottawa.bigbrainmoves.servio.presenters.AccountLoginPresenter;
import com.uottawa.bigbrainmoves.servio.repositories.Repository;
import com.uottawa.bigbrainmoves.servio.util.CurrentAccount;
import com.uottawa.bigbrainmoves.servio.repositories.DbHandler;
import com.uottawa.bigbrainmoves.servio.R;
import com.uottawa.bigbrainmoves.servio.util.UiUtil;
import com.uottawa.bigbrainmoves.servio.views.AccountLoginView;


public class SplashScreenActivity extends AppCompatActivity implements AccountLoginView {

    private final Repository repository = new DbHandler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

        AccountLoginPresenter presenter = new AccountLoginPresenter(this , repository);
        presenter.attemptGettingAccountInfo();

    }


    private void notLoggedIn() {
        Intent signUpOrLogin = new Intent(getApplicationContext(), LoginOrSignUpActivity.class);
        signUpOrLogin.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(signUpOrLogin);
    }


    /**
     * Method to be called when no account can be found
     */
    @Override
    public void displayNoAccountFound() {
        notLoggedIn();
    }

    /**
     * Method to be called when the database gets an error.
     */
    @Override
    public void displayDataError() {
        Toast.makeText(getApplicationContext(),
                "Insufficient permissions to communicate with database.",
                Toast.LENGTH_LONG).show();
        notLoggedIn();
    }

    /**
     * Method to be called when the account is found and valid.
     */
    @Override
    public void displayValidAccount() {
        Account account = CurrentAccount.getInstance().getCurrentAccount();
        // Account was not null to get here so we can assume that account will be non null.
        Toast.makeText(getApplicationContext(),"Logged In", Toast.LENGTH_LONG).show();

        Intent openCorrectUi = UiUtil.getIntentFromType(getApplicationContext(),
                account.getType());
        startActivity(openCorrectUi);
    }
}


