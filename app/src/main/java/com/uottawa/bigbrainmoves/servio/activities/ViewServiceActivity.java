package com.uottawa.bigbrainmoves.servio.activities;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.button.MaterialButton;
import com.uottawa.bigbrainmoves.servio.R;
import com.uottawa.bigbrainmoves.servio.models.ReadOnlyService;
import com.uottawa.bigbrainmoves.servio.models.ReadOnlyServiceProvider;
import com.uottawa.bigbrainmoves.servio.models.WeeklyAvailabilities;
import com.uottawa.bigbrainmoves.servio.presenters.ViewServicePresenter;
import com.uottawa.bigbrainmoves.servio.repositories.DbHandler;
import com.uottawa.bigbrainmoves.servio.repositories.Repository;
import com.uottawa.bigbrainmoves.servio.views.ViewServiceView;
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog;
import com.wdullaer.materialdatetimepicker.time.TimePickerDialog;
import com.wdullaer.materialdatetimepicker.time.Timepoint;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class ViewServiceActivity extends AppCompatActivity implements ViewServiceView {

    @BindView(R.id.serviceTypeText)
    TextView serviceTypeText;
    @BindView(R.id.licensedTextView)
    TextView licensedTextView;

    @BindView(R.id.mondayRangeTextView)
    TextView mondayRangeTextView;
    @BindView(R.id.tuesdayRangeTextView)
    TextView tuesdayRangeTextView;
    @BindView(R.id.wednesdayRangeTextView)
    TextView wednesdayRangeTextView;
    @BindView(R.id.thursdayRangeTextView)
    TextView thursdayRangeTextView;
    @BindView(R.id.fridayRangeTextView)
    TextView fridayRangeTextView;
    @BindView(R.id.saturdayRangeTextView)
    TextView saturdayRangeTextView;
    @BindView(R.id.sundayRangeTextView)
    TextView sundayRangeTextView;

    @BindView(R.id.ratingBar)
    RatingBar ratingBar;
    @BindView(R.id.providerNameTextView)
    TextView providerNameTextView;

    @BindView(R.id.btnViewProfile)
    MaterialButton btnViewProfile;
    @BindView(R.id.btnMakeBooking)
    MaterialButton btnMakeBooking;
    @BindView(R.id.btnViewRatings)
    MaterialButton btnViewRatings;

    private Button chooseTime;
    private TextView price;
    private TextView priceLabel;
    private TextView timeLabel;

    private ViewServicePresenter presenter;
    private final Repository repository = new DbHandler();
    private ReadOnlyServiceProvider serviceProvider = null;
    private ReadOnlyService service;
    private WeeklyAvailabilities availabilities;
    private Calendar selected = Calendar.getInstance();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_service);
        ButterKnife.bind(this);
        presenter = new ViewServicePresenter(repository, this);
        Intent intent = getIntent();
        service = intent.getParcelableExtra("service");
        presenter.loadAvailabilitiesForService(service);
        presenter.loadServiceProviderForService(service);
        displayServiceInfo(service);
    }

    private void onBookingClick() {
        if (availabilities == null) {
            Toast.makeText(getApplicationContext(),
                    "Please wait before clicking",
                    Toast.LENGTH_SHORT).show();
            return;
        }

        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        LayoutInflater inflater = this.getLayoutInflater();
        View dialogView  = inflater.inflate(R.layout.booking_dialog, null, false);

        Button chooseDate = dialogView.findViewById(R.id.btnChooseDate);

        chooseTime = dialogView.findViewById(R.id.btnSetTimeRange);
        price = dialogView.findViewById(R.id.priceTextView);
        timeLabel = dialogView.findViewById(R.id.timeRangeTextView);
        priceLabel = dialogView.findViewById(R.id.priceLabelTextView);

        Calendar calendar = Calendar.getInstance();
        chooseDate.setOnClickListener(__ -> {
            boolean timeVisible = chooseTime.getVisibility() == View.VISIBLE;
            boolean priceVisible = price.getVisibility() == View.VISIBLE;

            chooseTime.setVisibility(View.GONE);
            timeLabel.setVisibility(View.GONE);

            price.setVisibility(View.GONE);
            priceLabel.setVisibility(View.GONE);


            DatePickerDialog datePickerDialog = DatePickerDialog.newInstance((___, year, monthOfYear, dayOfMonth) -> {
                selected.set(year, monthOfYear, dayOfMonth);

                chooseTime.setText("Select a Time Range");
                price.setText("");
                chooseDate.setText(String.format(Locale.ENGLISH, "%04d-%02d-%02d", year, monthOfYear + 1, dayOfMonth));

                timeLabel.setVisibility(View.VISIBLE);
                chooseTime.setVisibility(View.VISIBLE);
            });

            datePickerDialog.setOnDismissListener(___ -> {
                if (timeVisible) {
                    timeLabel.setVisibility(View.VISIBLE);
                    chooseTime.setVisibility(View.VISIBLE);
                }
                if (priceVisible) {
                    priceLabel.setVisibility(View.VISIBLE);
                    price.setVisibility(View.VISIBLE);
                }
            });

            datePickerDialog.setMinDate(calendar);

            datePickerDialog.show(getSupportFragmentManager(),"DatePickerDialog");

        });

        chooseTime.setOnClickListener(__ -> {
            presenter.getTimeRestrictions(service, selected, availabilities);
        });

        dialogBuilder.setView(dialogView).setPositiveButton("Confirm Booking", (dialog, __) -> {
            String date = chooseDate.getText().toString();
            String timeRange = chooseTime.getText().toString();
            String priceText = price.getText().toString().replace("$", "");
            double priceVal = 0.0;
            if (!priceText.equals("")) {
                priceVal = Double.parseDouble(priceText);
            }

            presenter.createBooking(service, date, selected, timeRange, priceVal);
        }).setNegativeButton("Cancel", (dialog, __) -> {
            dialog.dismiss();
        }).setTitle("Make a Booking").show();



    }

    private void onViewRatingsClick() {

    }

    private void onViewProfileClick() {
        if (serviceProvider == null) {
            Toast.makeText(getApplicationContext(),
                    "Please wait while the provider is loaded",
                    Toast.LENGTH_SHORT).show();
            return;
        }
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        LayoutInflater inflater = this.getLayoutInflater();
        View dialogView  = inflater.inflate(R.layout.profile_info_dialog, null, false);
        dialogBuilder.setView(dialogView);

        AlertDialog dialog = dialogBuilder.create();
        Window window = dialog.getWindow();
        Button btnOk = dialogView.findViewById(R.id.btnOk);
        TextView phoneNum = dialogView.findViewById(R.id.providerPhoneNumber);
        TextView address = dialogView.findViewById(R.id.providerAddress);
        TextView company = dialogView.findViewById(R.id.providerCompany);
        TextView description = dialogView.findViewById(R.id.providerDescription);


        if (window != null) {
            window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

            phoneNum.setText(serviceProvider.getPhoneNumber());
            address.setText(serviceProvider.getAddress());
            company.setText(serviceProvider.getCompanyName());
            description.setText(serviceProvider.getDescription());

            btnOk.setOnClickListener(listener -> dialog.dismiss());

            dialog.show();
        }
    }

    @Override
    public void displayAvailabilities(WeeklyAvailabilities availabilities) {
        this.availabilities = availabilities;

        availabilitiesHelper(mondayRangeTextView, availabilities.getMondayStart(), availabilities.getMondayEnd());

        availabilitiesHelper(tuesdayRangeTextView, availabilities.getTuesdayStart(), availabilities.getTuesdayEnd());

        availabilitiesHelper(wednesdayRangeTextView, availabilities.getWednesdayStart(), availabilities.getWednesdayEnd());

        availabilitiesHelper(thursdayRangeTextView, availabilities.getThursdayStart(), availabilities.getThursdayEnd());

        availabilitiesHelper(fridayRangeTextView, availabilities.getFridayStart(), availabilities.getFridayEnd());

        availabilitiesHelper(saturdayRangeTextView, availabilities.getSaturdayStart(), availabilities.getSaturdayEnd());

        availabilitiesHelper(sundayRangeTextView, availabilities.getSundayStart(), availabilities.getSundayEnd());
    }

    private void availabilitiesHelper(TextView timeView, String startTime, String endTime) {
        if (startTime.matches("^(2[0-3]|[01]?[0-9]):([0-3]?[0])$")) {
            timeView.setText(String.format("%s~%s", startTime, endTime));
        } else {
            timeView.setText("None");
        }
    }

    @Override
    public void displayServiceProviderInfo(ReadOnlyServiceProvider provider) {
        serviceProvider = provider;
        String licensedText = provider.isLicensed() ? "Licensed" : "Not Licensed";
        licensedTextView.setText(licensedText);
    }


    public void displayServiceInfo(ReadOnlyService service) {
        serviceTypeText.setText(service.getType());
        providerNameTextView.setText(service.getServiceProviderName());
        ratingBar.setRating((float)service.getRating());
    }

    @Override
    public void displayDbError() {
        Toast.makeText(getApplicationContext(),
                "Database cannot be reached due to insufficient permissions.",
                Toast.LENGTH_LONG).show();
    }

    @Override
    public void displayAccountDoesNotExist() {
        Toast.makeText(getApplicationContext(),
                "The database was not able to get information about the service provider",
                Toast.LENGTH_LONG).show();
    }

    @Override
    public void displayNoAvailabilitiesOnThisDay() {
        Toast.makeText(getApplicationContext(), "The service provider has no availabilities on this day!", Toast.LENGTH_LONG).show();;
    }

    @Override
    public void displayAvailableTimes(String startTime, String endTime, List<String> unavailable) {
        List<Timepoint> timepoints = new ArrayList<>();
        for (String s : unavailable) {
            String[] splitTime = s.split(":");
            int hour = Integer.parseInt(splitTime[0]);
            int minute = Integer.parseInt(splitTime[1]);

            timepoints.add(new Timepoint(hour, minute));
        }

        Timepoint[] invalidTimePoints = timepoints.toArray(new Timepoint[0]);

        String[] startSplitTime = startTime.split(":");
        int startHour = Integer.parseInt(startSplitTime[0]);
        int startMin = Integer.parseInt(startSplitTime[1]);

        String[] endSplitTime = endTime.split(":");
        int endHour = Integer.parseInt(endSplitTime[0]);
        int endMin = Integer.parseInt(endSplitTime[1]);

        boolean priceVisible = price.getVisibility() == View.VISIBLE;

        priceLabel.setVisibility(View.GONE);
        price.setVisibility(View.GONE);

        int endHourRestrict = (endHour - (endMin == 0 ? 1 : 0));
        endHourRestrict = endHourRestrict < 0 ? 23 : endHourRestrict;
        int endMinRestrict = endMin == 0 ? 30 : 0;


        TimePickerDialog timePickerStart = TimePickerDialog.newInstance(
                (__, selectedStartHour, selectedStartMinute, ___) -> {
                    int selectedStartHourRestrict = (selectedStartHour + ((selectedStartMinute + 30) / 60)) % 24;
                    int selectedStartMinuteRestrict = (selectedStartMinute + 30) % 60;
                    TimePickerDialog timePickerEnd = TimePickerDialog.newInstance(
                            (____, selectedEndHour, selectedEndMinute, _____) -> {
                                price.setText("");
                                chooseTime.setText(String.format(Locale.ENGLISH,
                                        "%02d:%02d~%02d:%02d",
                                        selectedStartHour, selectedStartMinute,
                                        selectedEndHour, selectedEndMinute));

                                double duration = (selectedEndHour - selectedStartHour)
                                                 + (selectedEndMinute - selectedStartMinute) / 60.0;

                                presenter.getServicePrice(service, duration);
                            }, true);

                    timePickerEnd.setOnDismissListener(____ -> {
                        if (priceVisible) {
                            priceLabel.setVisibility(View.VISIBLE);
                            price.setVisibility(View.VISIBLE);

                        }
                    });

                    timePickerEnd.setMinTime(selectedStartHourRestrict, selectedStartMinuteRestrict, 0);
                    timePickerEnd.setMaxTime(endHour, endMin, 0);
                    timePickerEnd.setTimeInterval(1, 30);

                    timePickerEnd.setDisabledTimes(invalidTimePoints);

                    timePickerEnd.show(getSupportFragmentManager(), "TimePicker");
                }, true);

        timePickerStart.setOnDismissListener(__ -> {
            if (priceVisible) {
                priceLabel.setVisibility(View.VISIBLE);
                price.setVisibility(View.VISIBLE);
            }
        });

        timePickerStart.setMinTime(startHour, startMin, 0);
        timePickerStart.setMaxTime(endHourRestrict, endMinRestrict, 0);
        timePickerStart.setTimeInterval(1, 30);
        timePickerStart.setDisabledTimes(invalidTimePoints);
        timePickerStart.show(getSupportFragmentManager(), "TimePicker");
    }

    @Override
    public void displayPrice(double price) {
        priceLabel.setVisibility(View.VISIBLE);
        this.price.setVisibility(View.VISIBLE);
        this.price.setText(String.format(Locale.ENGLISH, "$%.2f", price));
    }

    @Override
    public void displayServiceTypeNoLongerOffered() {
        Toast.makeText(getApplicationContext(),
                "Sorry, services of this type are no longer offered",
                Toast.LENGTH_LONG).show();
    }

    //TODO GIVE THESE TO THE OTHERS
    @Override
    public void displayInvalidDate() {

    }

    @Override
    public void displayInvalidTime() {

    }

    @Override
    public void displayBookingCollision() {

    }

    @Override
    public void displayBookingCreated() {

    }

    @Override
    public void displayEmptyTime() {

    }


    @OnClick({R.id.btnViewProfile, R.id.btnMakeBooking, R.id.btnViewRatings})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.btnViewProfile:
                onViewProfileClick();
                break;
            case R.id.btnMakeBooking:
                onBookingClick();
                break;
            case R.id.btnViewRatings:
                onViewRatingsClick();
                break;
        }
    }
}
