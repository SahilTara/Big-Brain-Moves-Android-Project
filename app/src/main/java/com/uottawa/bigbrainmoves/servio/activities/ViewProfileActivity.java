package com.uottawa.bigbrainmoves.servio.activities;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputLayout;
import com.jaredrummler.materialspinner.MaterialSpinner;
import com.uottawa.bigbrainmoves.servio.R;
import com.uottawa.bigbrainmoves.servio.models.Service;
import com.uottawa.bigbrainmoves.servio.presenters.ViewProfilePresenter;
import com.uottawa.bigbrainmoves.servio.repositories.DbHandler;
import com.uottawa.bigbrainmoves.servio.repositories.Repository;
import com.uottawa.bigbrainmoves.servio.util.ServicesOfferableListAdapter;
import com.uottawa.bigbrainmoves.servio.util.ServicesOfferedListAdapter;
import com.uottawa.bigbrainmoves.servio.views.ViewProfileView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class ViewProfileActivity extends AppCompatActivity implements ViewProfileView {
    private Repository repository = new DbHandler();
    private ViewProfilePresenter presenter;

    private EditText phoneViewText;
    private EditText addressViewText;
    private EditText companyViewText;
    private EditText descriptionViewText;

    private TextInputLayout phoneInputLayout;
    private TextInputLayout addressInputLayout;
    private TextInputLayout companyInputLayout;

    private MaterialSpinner licenseViewText;
    private FloatingActionButton actionButton;

    private Drawable originalTextDrawable;

    private Drawable editIcon;
    private Drawable saveIcon;

    private RecyclerView provided;
    private RecyclerView providable;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_profile);

        phoneViewText = findViewById(R.id.phoneViewText);
        addressViewText = findViewById(R.id.addressViewText);
        companyViewText = findViewById(R.id.companyViewText);
        descriptionViewText = findViewById(R.id.descriptionViewText);
        licenseViewText = findViewById(R.id.licenseViewText);

        phoneInputLayout = findViewById(R.id.phoneInputLayout);
        addressInputLayout = findViewById(R.id.addressInputLayout);
        companyInputLayout = findViewById(R.id.companyInputLayout);

        actionButton = findViewById(R.id.floatingActionButton);

        provided = findViewById(R.id.servicesProvidedRecycler);
        providable = findViewById(R.id.servicesProvidableRecycler);

        originalTextDrawable = phoneViewText.getBackground();
        editIcon = actionButton.getDrawable();
        saveIcon = getDrawable(R.drawable.ic_save_red_24dp);
        licenseViewText.setItems("Not Licensed", "Licensed");

        setBackground(null);

        presenter = new ViewProfilePresenter(this, repository);
        presenter.showProfileInfo();
    }

    /**
     * Sets the background of all the edittexts to the specified drawable.
     * @param background the background to set.
     */
    private void setBackground(Drawable background) {
        phoneViewText.setBackground(background);
        addressViewText.setBackground(background);
        companyViewText.setBackground(background);
        descriptionViewText.setBackground(background);
    }

    /**
     * Sets the enabled state of all the editable components based on the boolean passed.
     * @param enabledState whether or not the components are enabled.
     */
    private void setEnabledState(boolean enabledState) {
        phoneViewText.setEnabled(enabledState);
        addressViewText.setEnabled(enabledState);
        companyViewText.setEnabled(enabledState);
        descriptionViewText.setEnabled(enabledState);
        licenseViewText.setEnabled(enabledState);
    }

    public void onFabClick(View view) {
        if (!isEditing()) {
            setEnabledState(true);
            setBackground(originalTextDrawable);
            actionButton.setImageDrawable(saveIcon);
            changeRecyclerViewState();
            RecyclerView recyclerView = findViewById(R.id.servicesProvidedRecycler);
        } else {
            ServicesOfferedListAdapter adapter = (ServicesOfferedListAdapter) provided.getAdapter();
            String phoneNumber = phoneViewText.getText().toString();
            String address = addressViewText.getText().toString();
            String companyName = companyViewText.getText().toString();
            String description = descriptionViewText.getText().toString();
            boolean isLicensed = licenseViewText.getText().equals("Licensed");

            List<Service> tmpServices = null;

            if (adapter != null) {
                tmpServices = new ArrayList<>(adapter.getServices());
            }

            if (tmpServices == null)
                tmpServices = Collections.emptyList();

            resetErrors();
            presenter.saveProfile(phoneNumber, address, companyName, description, isLicensed, tmpServices);
        }
    }

    /**
     * Method to figure out the current state of the activity i.e whether the user is in edit or display
     * mode.
     * @return Whether or not the user is in edit mode.
     */
    private boolean isEditing() {
        return actionButton.getDrawable().equals(saveIcon);
    }

    /**
     * Sets all text input layout error messages in current activity
     * to null when this method is called.
     */
    private void resetErrors() {
        phoneInputLayout.setError(null);
        addressInputLayout.setError(null);
        companyInputLayout.setError(null);
    }

    @Override
    public void displayServiceProviderInfo(String phoneNumber, String address, String companyName,
                                           String description, boolean isLicensed,
                                           List<Service> offered, List<Service> offerable) {

        phoneViewText.setText(phoneNumber);
        addressViewText.setText(address);
        companyViewText.setText(companyName);
        descriptionViewText.setText(description);
        licenseViewText.setSelectedIndex(isLicensed ? 1 : 0);

        initializeRecyclerView(offered, offerable);
    }

    private void changeRecyclerViewState() {
        List<Service> offered = null;
        List<Service> offerable = null;


        ServicesOfferedListAdapter offeredListAdapter = (ServicesOfferedListAdapter) provided.getAdapter();
        ServicesOfferableListAdapter offerableListAdapter = (ServicesOfferableListAdapter) providable.getAdapter();


        if (offeredListAdapter != null)
            offered = offeredListAdapter.getServices();

        if (offered == null)
            offered = Collections.emptyList();

        if (offerableListAdapter != null)
            offerable = offerableListAdapter.getServices();

        if (offerable == null)
            offerable = Collections.emptyList();

        offeredListAdapter = new ServicesOfferedListAdapter(offered, isEditing());
        offerableListAdapter = new ServicesOfferableListAdapter(offerable, isEditing());

        offeredListAdapter.setOtherAdapter(offerableListAdapter);
        offerableListAdapter.setOtherAdapter(offeredListAdapter);

        provided.setAdapter(offeredListAdapter);
        providable.setAdapter(offerableListAdapter);

        if (!isEditing()) {
            providable.setVisibility(View.GONE);
        } else {
            providable.setVisibility(View.VISIBLE);
        }
    }

    private void initializeRecyclerView(List<Service> offered, List<Service> offerable) {
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        ((LinearLayoutManager) layoutManager).setOrientation(RecyclerView.HORIZONTAL);

        RecyclerView.LayoutManager layoutManager1 = new LinearLayoutManager(this);
        ((LinearLayoutManager) layoutManager1).setOrientation(RecyclerView.HORIZONTAL);
        provided.setHasFixedSize(true);
        providable.setHasFixedSize(true);

        ServicesOfferedListAdapter offeredListAdapter = new ServicesOfferedListAdapter(offered, isEditing());
        ServicesOfferableListAdapter offerableListAdapter = new ServicesOfferableListAdapter(offerable, isEditing());

        offeredListAdapter.setOtherAdapter(offerableListAdapter);
        offerableListAdapter.setOtherAdapter(offeredListAdapter);

        provided.setAdapter(offeredListAdapter);
        provided.setLayoutManager(layoutManager);

        providable.setAdapter(offerableListAdapter);
        providable.setLayoutManager(layoutManager1);
    }

    @Override
    public void displayInvalidPhoneNumber() {
        phoneInputLayout.setError("Invalid Phone Number");
    }

    @Override
    public void displayInvalidAddress() {
        addressInputLayout.setError("Invalid Address Must be Numbers followed by spaces + letters");
    }

    @Override
    public void displayInvalidCompanyName() {
        companyInputLayout.setError("The company name must be entered");
    }

    public void displaySuccessfullySaved() {
        setEnabledState(false);
        setBackground(null);
        actionButton.setImageDrawable(editIcon);
        Toast.makeText(getApplicationContext(),
                "Successfully Saved",
                Toast.LENGTH_LONG).show();
        changeRecyclerViewState();
    }

    @Override
    public void displaySaveUnsuccessful() {
        Toast.makeText(getApplicationContext(),
                "Save unsuccessful due to parameters passed",
                Toast.LENGTH_LONG).show();
        
    }

    @Override
    public void displayDbError() {
        Toast.makeText(getApplicationContext(),
             "Save unsuccessful due to insufficient permissions",
             Toast.LENGTH_LONG).show();
    }
}
