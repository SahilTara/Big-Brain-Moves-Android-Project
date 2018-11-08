package com.uottawa.bigbrainmoves.servio.activites;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.uottawa.bigbrainmoves.servio.util.CurrentAccount;
import com.uottawa.bigbrainmoves.servio.R;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        TextView welcomeText = findViewById(R.id.welcomeMessageText);
        CurrentAccount currentAccount = CurrentAccount.getInstance();
        final String text = "Welcome " + currentAccount.getCurrentAccount().getDisplayName() +
                ", current role is Home Owner";
        welcomeText.setText(text);
    }

    public void onSignoutClick(View view) {
        FirebaseAuth.getInstance().signOut();
        CurrentAccount user = CurrentAccount.getInstance();
        user.setCurrentAccount(null);
        Intent intent = new Intent(getApplicationContext(), LoginOrSignUpActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }

}
