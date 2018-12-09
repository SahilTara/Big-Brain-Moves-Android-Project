package com.uottawa.bigbrainmoves.servio.activities;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.uottawa.bigbrainmoves.servio.R;
import com.uottawa.bigbrainmoves.servio.models.TimeSlot;
import com.uottawa.bigbrainmoves.servio.models.WeeklyAvailabilities;
import com.uottawa.bigbrainmoves.servio.presenters.ManageAvailabilitiesPresenter;
import com.uottawa.bigbrainmoves.servio.repositories.DbHandler;
import com.uottawa.bigbrainmoves.servio.repositories.Repository;
import com.uottawa.bigbrainmoves.servio.util.enums.DayOfWeek;
import com.uottawa.bigbrainmoves.servio.util.Pair;
import com.uottawa.bigbrainmoves.servio.util.enums.TimeSlotEntryType;
import com.uottawa.bigbrainmoves.servio.views.ManageAvailabilitiesView;
import com.wdullaer.materialdatetimepicker.time.TimePickerDialog;
import com.wdullaer.materialdatetimepicker.time.Timepoint;

import java.util.EnumMap;
import java.util.Locale;
import java.util.Map;

public class ManageAvailabilitiesActivity extends AppCompatActivity implements ManageAvailabilitiesView {

    ManageAvailabilitiesPresenter presenter;
    Repository repository = new DbHandler();
    private static final Map<DayOfWeek, Pair<Integer, Integer>> dayToButtonIdMap = createDayToButtonIdMap();


    private static Map<DayOfWeek, Pair<Integer, Integer>> createDayToButtonIdMap() {
        Map<DayOfWeek, Pair<Integer, Integer>> map = new EnumMap<>(DayOfWeek.class);

        map.put(DayOfWeek.MONDAY, new Pair<>(R.id.mondayStart, R.id.mondayEnd));
        map.put(DayOfWeek.TUESDAY, new Pair<>(R.id.tuesdayStart, R.id.tuesdayEnd));
        map.put(DayOfWeek.WEDNESDAY, new Pair<>(R.id.wednesdayStart, R.id.wednesdayEnd));
        map.put(DayOfWeek.THURSDAY, new Pair<>(R.id.thursdayStart, R.id.thursdayEnd));
        map.put(DayOfWeek.FRIDAY, new Pair<>(R.id.fridayStart, R.id.fridayEnd));
        map.put(DayOfWeek.SATURDAY, new Pair<>(R.id.saturdayStart, R.id.saturdayEnd));
        map.put(DayOfWeek.SUNDAY, new Pair<>(R.id.sundayStart, R.id.sundayEnd));

        return map;
    }

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
            Pair<DayOfWeek, TimeSlotEntryType> dayOfWeekAndType = tagConverter(tag);

            String defaultTime = dayOfWeekAndType.getSecond() == TimeSlotEntryType.START ? "Start Time" : "End Time";

            button.setText(defaultTime);

            presenter.setTime(defaultTime, dayOfWeekAndType.getFirst(), dayOfWeekAndType.getSecond());
        }).setNegativeButton("Cancel", (dialog, which) ->
            dialog.dismiss()
        ).show();
        return true;
    }

    public void onSaveClick(View view) {
        presenter.saveTimes();
    }

    private Pair<DayOfWeek, TimeSlotEntryType> tagConverter(String tag) {
        String[] dayAndTypePair = tag.split("_");

        DayOfWeek day = DayOfWeek.valueOf(dayAndTypePair[0]);
        TimeSlotEntryType type = TimeSlotEntryType.valueOf(dayAndTypePair[1]);

        return new Pair<>(day, type);
    }


    private void buildClock(Button button) {
        Pair<DayOfWeek, TimeSlotEntryType> dayOfWeekAndType = tagConverter(button.getTag().toString());

        TimePickerDialog timePickerDialog = TimePickerDialog.newInstance(
                (view, hourOfDay, minute, second) -> {
                    button.setText(String.format(Locale.ENGLISH, "%02d:%02d", hourOfDay,minute));

                    presenter.setTime(button.getText().toString(), dayOfWeekAndType.getFirst(),
                            dayOfWeekAndType.getSecond());
                }, true);

        timePickerDialog.setTimeInterval(1, 30);
        String buttonText = button.getText().toString();
        Pair<String, Boolean> timeRestriction = presenter.getTimeRestriction(dayOfWeekAndType.getFirst(),
                dayOfWeekAndType.getSecond());

        String[] splitTimeRestriction = timeRestriction.getFirst().split(":");
        int hourRestrict = Integer.parseInt(splitTimeRestriction[0]);
        int minRestrict = Integer.parseInt(splitTimeRestriction[1]);

        if (buttonText.contains(":")) {
            String[] splitTimeHoursAndMin = buttonText.split(":");
            int hour = Integer.parseInt(splitTimeHoursAndMin[0]);
            int min = Integer.parseInt(splitTimeHoursAndMin[1]);
            timePickerDialog.setInitialSelection(hour, min);
            if (timeRestriction.getSecond()) {
                timePickerDialog.setMaxTime(new Timepoint(23, 30));
            } else {
                timePickerDialog.setMinTime(new Timepoint(0, 0));
            }
        }


        // Starting restriction
        if (timeRestriction.getSecond()) {
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


        for (DayOfWeek dayOfWeek : DayOfWeek.values()) {
            Pair<Integer, Integer> pairOfStartAndEndIds = dayToButtonIdMap.get(dayOfWeek);

            if (dayOfWeek == DayOfWeek.ANY || pairOfStartAndEndIds == null)
                continue;

            TimeSlot slot = weeklyAvailabilities.getTimeSlotOnDay(dayOfWeek);

            Button start = findViewById(pairOfStartAndEndIds.getFirst());
            Button end = findViewById(pairOfStartAndEndIds.getSecond());

            start.setText(slot.getStartTime());
            end.setText(slot.getEndTime());
        }
    }

    @Override
    public void displayDayInvalid(DayOfWeek day) {
        Toast.makeText(getApplicationContext(),
                "If you set a start time you must set an end time for " + day.toString() +
                        " and vice versa", Toast.LENGTH_LONG).show();
    }
}
