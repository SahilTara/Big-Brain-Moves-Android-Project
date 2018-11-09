package com.uottawa.bigbrainmoves.servio.activites;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Toast;

import com.uottawa.bigbrainmoves.servio.R;
import com.uottawa.bigbrainmoves.servio.models.ServiceType;
import com.uottawa.bigbrainmoves.servio.presenters.ManageServiceTypesPresenter;
import com.uottawa.bigbrainmoves.servio.repositories.DbHandler;
import com.uottawa.bigbrainmoves.servio.repositories.Repository;
import com.uottawa.bigbrainmoves.servio.util.ServiceTypeListAdapter;
import com.uottawa.bigbrainmoves.servio.views.ManageServiceTypesView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ManageServiceTypesActivity extends AppCompatActivity implements ManageServiceTypesView  {

    ManageServiceTypesPresenter presenter;
    Repository repository = new DbHandler();
    List<ServiceType> listOfServices = new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_service_types);
        presenter = new ManageServiceTypesPresenter(this, repository);
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
