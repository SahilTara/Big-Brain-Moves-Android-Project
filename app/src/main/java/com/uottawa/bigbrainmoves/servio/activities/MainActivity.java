package com.uottawa.bigbrainmoves.servio.activities;

import android.content.Intent;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.uottawa.bigbrainmoves.servio.R;
import com.uottawa.bigbrainmoves.servio.presenters.MainScreenPresenter;
import com.uottawa.bigbrainmoves.servio.repositories.DbHandler;
import com.uottawa.bigbrainmoves.servio.repositories.Repository;
import com.uottawa.bigbrainmoves.servio.views.MainView;

public class MainActivity extends AppCompatActivity implements MainView {
    private Repository repository = new DbHandler();
    private MainScreenPresenter presenter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        presenter = new MainScreenPresenter(this, repository);
        presenter.showWelcomeMessage();
    }

    public void onFindServicesClick(View view) {
        Intent intent = new Intent(getApplicationContext(), FindServicesActivity.class);
        startActivity(intent);
    }

    public void onViewBookingsClick(View view) {
        Intent viewBookings = new Intent(getApplicationContext(), ViewBookingsActivity.class);
        startActivity(viewBookings);
    }

    public void onSignOutClick(View view) {
        presenter.signOut();
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
                ", current role is Home Owner";
        welcomeText.setText(text);
    }
}
