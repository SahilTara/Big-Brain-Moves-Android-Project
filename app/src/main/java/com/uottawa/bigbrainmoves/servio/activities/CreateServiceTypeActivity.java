package com.uottawa.bigbrainmoves.servio.activities;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.uottawa.bigbrainmoves.servio.R;
import com.uottawa.bigbrainmoves.servio.presenters.CreateServiceTypePresenter;
import com.uottawa.bigbrainmoves.servio.repositories.DbHandler;
import com.uottawa.bigbrainmoves.servio.repositories.Repository;
import com.uottawa.bigbrainmoves.servio.views.CreateServiceTypeView;

public class CreateServiceTypeActivity extends AppCompatActivity implements CreateServiceTypeView {
    private CreateServiceTypePresenter presenter;
    final Repository repository = new DbHandler();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_service_type);
        presenter = new CreateServiceTypePresenter(this, repository);
    }

    public void onCreateServiceTypeClick(View view) {
        EditText serviceTypeNameText = findViewById(R.id.serviceTypeNameText);
        EditText serviceTypeValueText = findViewById(R.id.serviceTypeValueText);
        String serviceTypeName = serviceTypeNameText.getText().toString();
        String serviceTypeValue = serviceTypeValueText.getText().toString();
        presenter.createServiceType(serviceTypeName, serviceTypeValue);
    }

    @Override
    public void displayInvalidName() {
        Toast.makeText(getApplicationContext(),
                "Name must not contain numbers, or symbols,"
                        + " and cannot be blank and must be <= 30 characters.",
                Toast.LENGTH_LONG).show();
    }

    @Override
    public void displayInvalidValue() {
        Toast.makeText(getApplicationContext(),
                "Value entered must be under or equal to 1.7 * 10^308 and not empty.",
                Toast.LENGTH_LONG).show();
    }

    @Override
    public void displayNameTaken() {
        Toast.makeText(getApplicationContext(),
                      "Service type already exists!",
                      Toast.LENGTH_LONG).show();
    }

    @Override
    public void displaySuccess() {
        Toast.makeText(getApplicationContext(),
                "Successfully created new service type.",
                Toast.LENGTH_LONG).show();
        finish();
    }

    @Override
    public void displayDataError() {
        Toast.makeText(getApplicationContext(),
                "Insufficient permissions to access database.",
                Toast.LENGTH_LONG).show();
        finish();
    }
}
