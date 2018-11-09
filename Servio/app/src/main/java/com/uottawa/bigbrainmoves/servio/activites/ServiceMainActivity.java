package com.uottawa.bigbrainmoves.servio.activites;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.uottawa.bigbrainmoves.servio.presenters.MainScreenPresenter;
import com.uottawa.bigbrainmoves.servio.repositories.DbHandler;
import com.uottawa.bigbrainmoves.servio.repositories.Repository;
import com.uottawa.bigbrainmoves.servio.util.CurrentAccount;
import com.uottawa.bigbrainmoves.servio.R;
import com.uottawa.bigbrainmoves.servio.views.MainView;

public class ServiceMainActivity extends AppCompatActivity implements MainView  {
    MainScreenPresenter presenter;
    Repository repository = new DbHandler();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_service_main);
        presenter = new MainScreenPresenter(this, repository);
        presenter.showWelcomeMessage();
    }

    public void onSignOutClick(View view) {
        presenter.signOut();
    }


    @Override
    public void displaySignOut() {
        //TODO: TOAST SAYING "Successfully Signed Out."
        Intent intent = new Intent(getApplicationContext(), LoginOrSignUpActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }

    @Override
    public void displayWelcomeText(String displayName) {
        TextView welcomeText = findViewById(R.id.welcomeMessageText);

        final String text = "Welcome " + displayName +
                ", current role is Service Owner";

        welcomeText.setText(text);
    }
}
