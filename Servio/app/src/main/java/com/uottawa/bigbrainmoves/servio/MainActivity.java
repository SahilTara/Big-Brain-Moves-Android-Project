package com.uottawa.bigbrainmoves.servio;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        TextView welcomeText = findViewById(R.id.welcomeMessageText);
        CurrentUser currentUser = new CurrentUser();
        final String text = "Welcome " + currentUser.getCurrentUser().getDisplayName() +
                ", current role is Home Owner";
        welcomeText.setText(text);
    }

    public void onSignoutClick(View view) {
        FirebaseAuth.getInstance().signOut();
        CurrentUser user = new CurrentUser();
        user.setCurrentUser(null);
        Intent intent = new Intent(getApplicationContext(), LoginOrSignUpActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }

}
