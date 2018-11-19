package com.uottawa.bigbrainmoves.servio.activities;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.uottawa.bigbrainmoves.servio.R;
import com.uottawa.bigbrainmoves.servio.presenters.ManageAvailabilitiesPresenter;
import com.uottawa.bigbrainmoves.servio.repositories.DbHandler;
import com.uottawa.bigbrainmoves.servio.repositories.Repository;
import com.uottawa.bigbrainmoves.servio.util.DayOfWeek;
import com.uottawa.bigbrainmoves.servio.util.Pair;
import com.uottawa.bigbrainmoves.servio.util.TimeUtil;
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

            if (tag.toLowerCase().contains("end")) {
                button.setText("End Time");
            } else {
                button.setText("Start Time");
            }

            presenter.setTime("", tag);
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
                    presenter.setTime(button.getText().toString(), button.getTag().toString());
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
    public void displaySuccesfulSave() {
        Toast.makeText(getApplicationContext(),
                "Successfully saved availabilities",
                Toast.LENGTH_LONG).show();
    }

    @Override
    public void displayTimes(String mondayStart, String mondayEnd,
                             String tuesdayStart, String tuesdayEnd,
                             String wednesdayStart, String wednesdayEnd,
                             String thursdayStart, String thursdayEnd,
                             String fridayStart, String fridayEnd,
                             String saturdayStart, String saturdayEnd,
                             String sundayStart, String sundayEnd) {

        // Monday
        Button btnMondayStart = findViewById(R.id.mondayStart);
        btnMondayStart.setText(mondayStart);

        Button btnMondayEnd = findViewById(R.id.mondayEnd);
        btnMondayEnd.setText(mondayEnd);

        // Tuesday
        Button btnTuesdayStart = findViewById(R.id.tuesdayStart);
        btnTuesdayStart.setText(tuesdayStart);

        Button btnTuesdayEnd = findViewById(R.id.tuesdayEnd);
        btnTuesdayEnd.setText(tuesdayEnd);

        // Wednesday
        Button btnWednesdayStart = findViewById(R.id.wednesdayStart);
        btnWednesdayStart.setText(wednesdayStart);

        Button btnWednesdayEnd = findViewById(R.id.wednesdayEnd);
        btnWednesdayEnd.setText(wednesdayEnd);

        // Thursday
        Button btnThursdayStart = findViewById(R.id.thursdayStart);
        btnThursdayStart.setText(thursdayStart);

        Button btnThursdayEnd = findViewById(R.id.thursdayEnd);
        btnThursdayEnd.setText(thursdayEnd);

        // Friday
        Button btnFridayStart = findViewById(R.id.fridayStart);
        btnFridayStart.setText(fridayStart);

        Button btnFridayEnd = findViewById(R.id.fridayEnd);
        btnFridayEnd.setText(fridayEnd);

        // Saturday
        Button btnSaturdayStart = findViewById(R.id.saturdayStart);
        btnSaturdayStart.setText(saturdayStart);

        Button btnSaturdayEnd = findViewById(R.id.saturdayEnd);
        btnSaturdayEnd.setText(saturdayEnd);

        // Sunday
        Button btnSundayStart = findViewById(R.id.sundayStart);
        btnSundayStart.setText(sundayStart);

        Button btnSundayEnd = findViewById(R.id.sundayEnd);
        btnSundayEnd.setText(sundayEnd);

    }

    @Override
    public void displayDayInvalid(DayOfWeek day) {
        Toast.makeText(getApplicationContext(),
                "If you set a start time you must set an end time for " + day.toString() +
                        " and vice versa", Toast.LENGTH_LONG).show();
    }
}
