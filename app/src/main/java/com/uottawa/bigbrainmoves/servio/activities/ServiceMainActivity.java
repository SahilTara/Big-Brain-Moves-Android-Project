package com.uottawa.bigbrainmoves.servio.activities;

import android.content.Intent;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.uottawa.bigbrainmoves.servio.presenters.MainScreenPresenter;
import com.uottawa.bigbrainmoves.servio.repositories.DbHandler;
import com.uottawa.bigbrainmoves.servio.repositories.Repository;
import com.uottawa.bigbrainmoves.servio.R;
import com.uottawa.bigbrainmoves.servio.views.MainView;
import com.uottawa.bigbrainmoves.servio.views.ViewProfileView;

public class ServiceMainActivity extends AppCompatActivity implements MainView  {
    private MainScreenPresenter presenter;
    private Repository repository = new DbHandler();
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

    public void onViewProfileClick(View view) {
        Intent viewProfile = new Intent(getApplicationContext(), ViewProfileActivity.class);
        startActivity(viewProfile);
    }

    public void onManageAvailabilitiesClick(View view) {
        Intent manageAvailabilities = new Intent(getApplicationContext(), ManageAvailabilitiesActivity.class);
        startActivity(manageAvailabilities);
    }

    @Override
    public void displaySignOut() {
        Toast.makeText(getApplicationContext(),
                "Successfully Signed Out.",
                Toast.LENGTH_LONG).show();
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
