package com.uottawa.bigbrainmoves.servio.activities;

import android.content.Intent;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.View;
import android.widget.Toast;

import com.uottawa.bigbrainmoves.servio.R;
import com.uottawa.bigbrainmoves.servio.models.ServiceType;
import com.uottawa.bigbrainmoves.servio.presenters.ManageServiceTypesPresenter;
import com.uottawa.bigbrainmoves.servio.repositories.DbHandler;
import com.uottawa.bigbrainmoves.servio.repositories.Repository;
import com.uottawa.bigbrainmoves.servio.util.adapters.ServiceTypeListAdapter;
import com.uottawa.bigbrainmoves.servio.views.ManageServiceTypesView;

import java.util.ArrayList;
import java.util.List;

public class ManageServiceTypesActivity extends AppCompatActivity implements ManageServiceTypesView  {
    private final Repository repository = new DbHandler();
    private List<ServiceType> listOfServices = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_service_types);
        ManageServiceTypesPresenter presenter = new ManageServiceTypesPresenter(this, repository);
        presenter.listenForServiceTypeUpdates();
    }

    public void onCreateServiceTypeClick(View view) {
        Intent activity = new Intent(getApplicationContext(), CreateServiceTypeActivity.class);
        startActivity(activity);
    }

    public void displayDataError() {
        Toast.makeText(getApplicationContext(),
                "Unable to retrieve service types list due to insufficient permissions.",
                Toast.LENGTH_LONG).show();
    }

    public void displayServiceTypeUpdate(ServiceType serviceType, boolean removed) {
        listOfServices.remove(serviceType);
        if (!removed)
            listOfServices.add(serviceType);
        RecyclerView recyclerView = findViewById(R.id.serviceTypeRecycler);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setHasFixedSize(true);
        RecyclerView.Adapter adapter  = new ServiceTypeListAdapter(listOfServices, this);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(layoutManager);
    }

}
