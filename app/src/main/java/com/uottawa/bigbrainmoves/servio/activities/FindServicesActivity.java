package com.uottawa.bigbrainmoves.servio.activities;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.RatingBar;

import com.jaredrummler.materialspinner.MaterialSpinner;
import com.pchmn.materialchips.ChipsInput;
import com.pchmn.materialchips.model.ChipInterface;
import com.uottawa.bigbrainmoves.servio.R;
import com.uottawa.bigbrainmoves.servio.models.Service;
import com.uottawa.bigbrainmoves.servio.models.ServiceType;
import com.uottawa.bigbrainmoves.servio.presenters.FindServicesPresenter;
import com.uottawa.bigbrainmoves.servio.repositories.DbHandler;
import com.uottawa.bigbrainmoves.servio.repositories.Repository;
import com.uottawa.bigbrainmoves.servio.util.enums.DayOfWeek;
import com.uottawa.bigbrainmoves.servio.util.adapters.ServiceListAdapter;
import com.uottawa.bigbrainmoves.servio.util.ServiceTypeChip;
import com.uottawa.bigbrainmoves.servio.views.FindServicesView;
import com.wdullaer.materialdatetimepicker.time.TimePickerDialog;

import java.util.ArrayList;
import java.util.List;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class FindServicesActivity extends AppCompatActivity implements FindServicesView {
    private ChipsInput searchBar;
    FindServicesPresenter presenter;
    Repository repository = new DbHandler();
    List<ChipInterface> chips = new ArrayList<>();
    List<Service> listOfServices = new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_find_services);
        searchBar = findViewById(R.id.searchBar);
        searchBar.addChipsListener(new ChipsInput.ChipsListener() {
            @Override
            public void onChipAdded(ChipInterface chipInterface, int i) {
                presenter.addToFilterString(listOfServices, chipInterface.getLabel());
            }

            @Override
            public void onChipRemoved(ChipInterface chipInterface, int i) {
                presenter.removeFromFilterString(listOfServices, chipInterface.getLabel());
            }

            @Override
            public void onTextChanged(CharSequence charSequence) {
                /*
                 * No code is entered here since we do not care about onTextChanged event, however
                 * the interface requires this to be overridden.
                 *
                 */
            }
        });
        presenter = new FindServicesPresenter(this, repository);
        presenter.listenForServiceTypeChanges();
        presenter.listenForServiceChanges();
    }


    public void onFilterClick(View view) {
        AlertDialog.Builder filterDialog = new AlertDialog.Builder(this);
        LayoutInflater inflater = this.getLayoutInflater();
        View filterDialogView = inflater.inflate(R.layout.filter_dialog, null, false);
        filterDialog.setView(filterDialogView);

        Button timeButton = filterDialogView.findViewById(R.id.btnSetStartTime);
        RatingBar ratingBar = filterDialogView.findViewById(R.id.filterByStars);

        timeButton.setOnClickListener(v -> {
            TimePickerDialog timePickerDialog = TimePickerDialog.newInstance(
                    (theView, hourOfDay, minute, second) -> {
                        timeButton.setText(String.format("%02d:%02d", hourOfDay,minute));
                    }, true);
            timePickerDialog.setTimeInterval(1, 30);
            timePickerDialog.show(getSupportFragmentManager(), "Timepickerdialog");
        });


        MaterialSpinner spinner = filterDialogView.findViewById(R.id.spinnerChooseDayOfWeek);

        spinner.setItems(DayOfWeek.ANY, DayOfWeek.MONDAY, DayOfWeek.TUESDAY, DayOfWeek.WEDNESDAY,
                DayOfWeek.THURSDAY, DayOfWeek.FRIDAY, DayOfWeek.SATURDAY, DayOfWeek.SUNDAY);



        filterDialog.setTitle("Filter").setPositiveButton("Apply Filter", (dialog, which) -> {
            dialog.dismiss();
            double rating = ratingBar.getRating();
            String time = timeButton.getText().toString();
            boolean filterByTime = time.matches("\\d{2}:\\d{2}");
            int selectedIndex = spinner.getSelectedIndex();

            DayOfWeek dayOfWeek = (DayOfWeek) spinner.getItems().get(selectedIndex);

            presenter.setExtraFilters(listOfServices, rating, filterByTime, time, dayOfWeek);
        }).setNegativeButton("Cancel", (dialog, which) -> {
            dialog.dismiss();
        }).show();
    }

    @Override
    public void displayLiveServicesInSearch(ServiceType type, boolean isDeleted) {

        if (isDeleted) {
            searchBar.removeChipByLabel(type.getType());
            chips.remove(new ServiceTypeChip(type.getType(), type.getType()));
        } else {
            chips.add(new ServiceTypeChip(type.getType(), type.getType()));
        }

        searchBar.setFilterableList(chips);
    }

    @Override
    public void displayFiltered(List<Service> services) {
        RecyclerView recyclerView = findViewById(R.id.filteredServicesRecycler);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setHasFixedSize(true);
        RecyclerView.Adapter adapter  = new ServiceListAdapter(services, this);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(layoutManager);
    }

    @Override
    public void displayLiveServices(Service service, boolean isDeleted) {
        listOfServices.remove(service);
        if (!isDeleted && service.isOffered())
            listOfServices.add(service);

        presenter.filterList(listOfServices);
    }
}
