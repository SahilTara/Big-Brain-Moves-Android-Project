package com.uottawa.bigbrainmoves.servio.activities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.uottawa.bigbrainmoves.servio.presenters.MainScreenPresenter;
import com.uottawa.bigbrainmoves.servio.repositories.DbHandler;
import com.uottawa.bigbrainmoves.servio.repositories.Repository;
import com.uottawa.bigbrainmoves.servio.R;
import com.uottawa.bigbrainmoves.servio.views.MainView;

public class AdminMainActivity extends AppCompatActivity implements MainView {
    private MainScreenPresenter presenter;
    private final Repository repository = new DbHandler();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_main);
        presenter = new MainScreenPresenter(this, repository);
        presenter.showWelcomeMessage();
    }

    public void onSignOutClick(View view) {
        presenter.signOut();
    }

    public void onUserListClick(View view) {
        Intent intent = new Intent(getApplicationContext(), UserListActivity.class);
        startActivity(intent);
    }

    public void onManageServiceTypesClick(View view) {
        Intent intent = new Intent(getApplicationContext(), ManageServiceTypesActivity.class);
        startActivity(intent);
    }

    @Override
    public void displaySignOut() {
        Toast.makeText(getApplicationContext(),
                       "Successfully signed out.",
                       Toast.LENGTH_LONG).show();
        Intent intent = new Intent(getApplicationContext(), LoginOrSignUpActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }

    @Override
    public void displayWelcomeText(String displayName) {
        TextView welcomeText = findViewById(R.id.welcomeMessageText);
        final String text = "Welcome " + displayName +
                ", current role is Admin";
        welcomeText.setText(text);
    }
}
