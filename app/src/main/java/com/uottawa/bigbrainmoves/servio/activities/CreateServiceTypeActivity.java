package com.uottawa.bigbrainmoves.servio.activities;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.dd.processbutton.iml.ActionProcessButton;
import com.google.android.material.textfield.TextInputLayout;
import com.uottawa.bigbrainmoves.servio.R;
import com.uottawa.bigbrainmoves.servio.presenters.CreateServiceTypePresenter;
import com.uottawa.bigbrainmoves.servio.repositories.DbHandler;
import com.uottawa.bigbrainmoves.servio.repositories.Repository;
import com.uottawa.bigbrainmoves.servio.views.CreateServiceTypeView;

public class CreateServiceTypeActivity extends AppCompatActivity implements CreateServiceTypeView {
    private CreateServiceTypePresenter presenter;
    final Repository repository = new DbHandler();

    private EditText serviceTypeNameText;
    private EditText serviceTypeValueText;

    private TextInputLayout serviceTypeNameInputLayout;
    private TextInputLayout serviceTypeValueInputLayout;

    private ActionProcessButton btnCreateServiceType;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_service_type);
        presenter = new CreateServiceTypePresenter(this, repository);

        serviceTypeNameText = findViewById(R.id.serviceTypeNameText);
        serviceTypeValueText = findViewById(R.id.serviceTypeValueText);

        serviceTypeNameInputLayout = findViewById(R.id.serviceTypeNameInputLayout);
        serviceTypeValueInputLayout = findViewById(R.id.serviceTypeValueInputLayout);

        btnCreateServiceType = findViewById(R.id.btnCreateServiceType);
        btnCreateServiceType.setMode(ActionProcessButton.Mode.ENDLESS);

        serviceTypeNameText.setOnFocusChangeListener((view, hasFocus) -> {
            if (hasFocus)
                btnCreateServiceType.setProgress(0);
        });

        serviceTypeValueText.setOnFocusChangeListener((view, hasFocus) -> {
            if (hasFocus)
                btnCreateServiceType.setProgress(0);
        });

    }

    public void onCreateServiceTypeClick(View view) {
        setEnabled(false);
        resetErrors();
        btnCreateServiceType.setProgress(1);
        String serviceTypeName = serviceTypeNameText.getText().toString();
        String serviceTypeValue = serviceTypeValueText.getText().toString();
        presenter.createServiceType(serviceTypeName, serviceTypeValue);
    }

    /**
     * Method sets the enable state of btnCreateServiceType, and the many texts.
     * @param isEnabled specifies whether components should be enabled or disabled.
     */
    private void setEnabled(boolean isEnabled) {
        btnCreateServiceType.setEnabled(isEnabled);
        serviceTypeNameText.setEnabled(isEnabled);
        serviceTypeValueText.setEnabled(isEnabled);
    }

    /**
     * Sets all text input layout error messages in current activity
     * to null when this method is called.
     */
    private void resetErrors() {
        serviceTypeNameInputLayout.setError(null);
        serviceTypeValueInputLayout.setError(null);
    }


    @Override
    public void displayInvalidName() {
        serviceTypeNameInputLayout.setError("Name must not contain numbers, or symbols,"
                + " and cannot be blank and must be <= 25 characters.");

        setEnabled(true);
        btnCreateServiceType.setProgress(-1);
    }

    @Override
    public void displayInvalidValue() {
        serviceTypeValueInputLayout.setError(
                "Value entered must be under or equal to 1.7 * 10^308 and not empty."
        );

        setEnabled(true);
        btnCreateServiceType.setProgress(-1);
    }

    @Override
    public void displayNameTaken() {
        serviceTypeNameInputLayout.setError("Service type with this name already exists.");

        setEnabled(true);
        btnCreateServiceType.setProgress(-1);
    }

    @Override
    public void displaySuccess() {
        Toast.makeText(getApplicationContext(),
                "Successfully created new service type.",
                Toast.LENGTH_LONG).show();
        btnCreateServiceType.setProgress(100);
        finish();
    }

    @Override
    public void displayDataError() {
        Toast.makeText(getApplicationContext(),
                "Insufficient permissions to access database.",
                Toast.LENGTH_LONG).show();
        btnCreateServiceType.setProgress(-1);
        finish();
    }
}
