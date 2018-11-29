package com.uottawa.bigbrainmoves.servio.activities;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.uottawa.bigbrainmoves.servio.R;
import com.uottawa.bigbrainmoves.servio.models.WeeklyAvailabilities;
import com.uottawa.bigbrainmoves.servio.presenters.ManageAvailabilitiesPresenter;
import com.uottawa.bigbrainmoves.servio.repositories.DbHandler;
import com.uottawa.bigbrainmoves.servio.repositories.Repository;
import com.uottawa.bigbrainmoves.servio.util.enums.DayOfWeek;
import com.uottawa.bigbrainmoves.servio.util.Pair;
import com.uottawa.bigbrainmoves.servio.views.ManageAvailabilitiesView;
import com.wdullaer.materialdatetimepicker.time.TimePickerDialog;
import com.wdullaer.materialdatetimepicker.time.Timepoint;

public class ManageAvailabilitiesActivity extends AppCompatActivity implements ManageAvailabilitiesView {

    ManageAvailabilitiesPresenter presenter;
    Repository repository = new DbHandler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_availabilities);
        presenter = new ManageAvailabilitiesPresenter(this, repository);
        presenter.getAvailabilities();

        // Monday
        Button btnMondayStart = findViewById(R.id.mondayStart);
        btnMondayStart.setOnLongClickListener(this::onTimeButtonLongClick);

        Button btnMondayEnd = findViewById(R.id.mondayEnd);
        btnMondayEnd.setOnLongClickListener(this::onTimeButtonLongClick);

        // Tuesday
        Button btnTuesdayStart = findViewById(R.id.tuesdayStart);
        btnTuesdayStart.setOnLongClickListener(this::onTimeButtonLongClick);

        Button btnTuesdayEnd = findViewById(R.id.tuesdayEnd);
        btnTuesdayEnd.setOnLongClickListener(this::onTimeButtonLongClick);

        // Wednesday
        Button btnWednesdayStart = findViewById(R.id.wednesdayStart);
        btnWednesdayStart.setOnLongClickListener(this::onTimeButtonLongClick);

        Button btnWednesdayEnd = findViewById(R.id.wednesdayEnd);
        btnWednesdayEnd.setOnLongClickListener(this::onTimeButtonLongClick);

        // Thursday
        Button btnThursdayStart = findViewById(R.id.thursdayStart);
        btnThursdayStart.setOnLongClickListener(this::onTimeButtonLongClick);

        Button btnThursdayEnd = findViewById(R.id.thursdayEnd);
        btnThursdayEnd.setOnLongClickListener(this::onTimeButtonLongClick);

        // Friday
        Button btnFridayStart = findViewById(R.id.fridayStart);
        btnFridayStart.setOnLongClickListener(this::onTimeButtonLongClick);

        Button btnFridayEnd = findViewById(R.id.fridayEnd);
        btnFridayEnd.setOnLongClickListener(this::onTimeButtonLongClick);

        // Saturday
        Button btnSaturdayStart = findViewById(R.id.saturdayStart);
        btnSaturdayStart.setOnLongClickListener(this::onTimeButtonLongClick);

        Button btnSaturdayEnd = findViewById(R.id.saturdayEnd);
        btnSaturdayEnd.setOnLongClickListener(this::onTimeButtonLongClick);

        // Sunday
        Button btnSundayStart = findViewById(R.id.sundayStart);
        btnSundayStart.setOnLongClickListener(this::onTimeButtonLongClick);

        Button btnSundayEnd = findViewById(R.id.sundayEnd);
        btnSundayEnd.setOnLongClickListener(this::onTimeButtonLongClick);
    }

    public void onTimeButtonClick(View view) {
        Button button = (Button) view;
        buildClock(button);
    }

    public boolean onTimeButtonLongClick(View view) {
        AlertDialog.Builder alertBuilder = new AlertDialog.Builder(this);
        alertBuilder.setTitle("Delete time slot?").setPositiveButton("Delete Time", (dialog, which) -> {
            dialog.dismiss();
            Button button = (Button) view;
            String tag = (String)button.getTag();

            WeeklyAvailabilities.TimeSlot timeSlot = WeeklyAvailabilities.TimeSlot.valueOf(tag);
            String defaultTime = timeSlot.name().toLowerCase().contains("start") ? "Start Time" : "End Time";

            button.setText(defaultTime);

            presenter.setTime(defaultTime, timeSlot);
        }).setNegativeButton("Cancel", (dialog, which) -> {
            dialog.dismiss();
        }).show();
        return true;
    }

    public void onSaveClick(View view) {
        presenter.saveTimes();
    }

    private void buildClock(Button button) {
        TimePickerDialog timePickerDialog = TimePickerDialog.newInstance(
                (view, hourOfDay, minute, second) -> {
                    button.setText(String.format("%02d:%02d", hourOfDay,minute));
                    presenter.setTime(button.getText().toString(),
                            WeeklyAvailabilities.TimeSlot.valueOf(button.getTag().toString()));
                }, true);

        timePickerDialog.setTimeInterval(1, 30);
        String buttonText = button.getText().toString();
        Pair<String, Boolean> timeRestriction = presenter.getTimeRestriction(button.getTag().toString());
        String[] splitTimeRestriction = timeRestriction.first.split(":");
        int hourRestrict = Integer.valueOf(splitTimeRestriction[0]);
        int minRestrict = Integer.valueOf(splitTimeRestriction[1]);

        if (buttonText.contains(":")) {
            String[] splitTimeHoursAndMin = buttonText.split(":");
            int hour = Integer.valueOf(splitTimeHoursAndMin[0]);
            int min = Integer.valueOf(splitTimeHoursAndMin[1]);
            timePickerDialog.setInitialSelection(hour, min);
            if (timeRestriction.second) {
                timePickerDialog.setMaxTime(new Timepoint(23, 30));
            } else {
                timePickerDialog.setMinTime(new Timepoint(0, 0));
            }
        }


        // Starting restriction
        if (timeRestriction.second) {
            hourRestrict = (hourRestrict + ((minRestrict + 30) / 60)) % 24;
            minRestrict = (minRestrict + 30) % 60;
            timePickerDialog.setMinTime(new Timepoint(hourRestrict, minRestrict));
        } else { // end restriction
            hourRestrict = (hourRestrict - (minRestrict == 0 ? 1 : 0));
            hourRestrict = hourRestrict < 0 ? 23 : hourRestrict;
            minRestrict = minRestrict == 0 ? 30 : 0;
            timePickerDialog.setMaxTime(new Timepoint(hourRestrict, minRestrict));
        }

        timePickerDialog.show(getSupportFragmentManager(), "Timepickerdialog");
    }

    @Override
    public void displayDbError() {
        Toast.makeText(getApplicationContext(),
                "Insufficient permissions to connect to database. Please relaunch.",
                Toast.LENGTH_LONG).show();
    }

    @Override
    public void displaySuccessfulSave() {
        Toast.makeText(getApplicationContext(),
                "Successfully saved availabilities",
                Toast.LENGTH_LONG).show();
    }

    @Override
    public void displayTimes(WeeklyAvailabilities weeklyAvailabilities) {

        // Monday
        Button btnMondayStart = findViewById(R.id.mondayStart);
        btnMondayStart.setText(weeklyAvailabilities.getMondayStart());

        Button btnMondayEnd = findViewById(R.id.mondayEnd);
        btnMondayEnd.setText(weeklyAvailabilities.getMondayEnd());

        // Tuesday
        Button btnTuesdayStart = findViewById(R.id.tuesdayStart);
        btnTuesdayStart.setText(weeklyAvailabilities.getTuesdayStart());

        Button btnTuesdayEnd = findViewById(R.id.tuesdayEnd);
        btnTuesdayEnd.setText(weeklyAvailabilities.getTuesdayEnd());

        // Wednesday
        Button btnWednesdayStart = findViewById(R.id.wednesdayStart);
        btnWednesdayStart.setText(weeklyAvailabilities.getWednesdayStart());

        Button btnWednesdayEnd = findViewById(R.id.wednesdayEnd);
        btnWednesdayEnd.setText(weeklyAvailabilities.getWednesdayEnd());

        // Thursday
        Button btnThursdayStart = findViewById(R.id.thursdayStart);
        btnThursdayStart.setText(weeklyAvailabilities.getThursdayStart());

        Button btnThursdayEnd = findViewById(R.id.thursdayEnd);
        btnThursdayEnd.setText(weeklyAvailabilities.getThursdayEnd());

        // Friday
        Button btnFridayStart = findViewById(R.id.fridayStart);
        btnFridayStart.setText(weeklyAvailabilities.getFridayStart());

        Button btnFridayEnd = findViewById(R.id.fridayEnd);
        btnFridayEnd.setText(weeklyAvailabilities.getFridayEnd());

        // Saturday
        Button btnSaturdayStart = findViewById(R.id.saturdayStart);
        btnSaturdayStart.setText(weeklyAvailabilities.getSaturdayStart());

        Button btnSaturdayEnd = findViewById(R.id.saturdayEnd);
        btnSaturdayEnd.setText(weeklyAvailabilities.getSaturdayEnd());

        // Sunday
        Button btnSundayStart = findViewById(R.id.sundayStart);
        btnSundayStart.setText(weeklyAvailabilities.getSundayStart());

        Button btnSundayEnd = findViewById(R.id.sundayEnd);
        btnSundayEnd.setText(weeklyAvailabilities.getSundayEnd());

    }

    @Override
    public void displayDayInvalid(DayOfWeek day) {
        Toast.makeText(getApplicationContext(),
                "If you set a start time you must set an end time for " + day.toString() +
                        " and vice versa", Toast.LENGTH_LONG).show();
    }
}
