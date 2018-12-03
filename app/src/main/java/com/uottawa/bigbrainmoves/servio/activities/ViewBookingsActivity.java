package com.uottawa.bigbrainmoves.servio.activities;

import android.os.Bundle;
import android.widget.Toast;

import com.uottawa.bigbrainmoves.servio.R;
import com.uottawa.bigbrainmoves.servio.models.Booking;
import com.uottawa.bigbrainmoves.servio.presenters.ViewBookingsPresenter;
import com.uottawa.bigbrainmoves.servio.repositories.DbHandler;
import com.uottawa.bigbrainmoves.servio.repositories.Repository;
import com.uottawa.bigbrainmoves.servio.util.adapters.BookingListAdapter;
import com.uottawa.bigbrainmoves.servio.views.ViewBookingsView;

import java.util.ArrayList;
import java.util.List;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class ViewBookingsActivity extends AppCompatActivity implements ViewBookingsView {

    private ViewBookingsPresenter presenter;
    private final Repository repository = new DbHandler();
    private List<Booking> listOfBookings = new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_bookings);
        presenter = new ViewBookingsPresenter(this, repository);
        presenter.listenForBookingUpdates();
    }

    public void displayDataError() {
        Toast.makeText(getApplicationContext(),
                "Unable to retrieve booking types list due to insufficient permissions.",
                Toast.LENGTH_LONG).show();
    }

    public void displayBookingUpdate(Booking booking, boolean removed) {
        listOfBookings.removeIf(item -> item.equals(booking));
        if (!removed)
            listOfBookings.add(booking);

        RecyclerView recyclerView = findViewById(R.id.bookingRecyclerView);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setHasFixedSize(true);
        RecyclerView.Adapter adapter  = new BookingListAdapter(listOfBookings, this.getLayoutInflater(), this);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(layoutManager);
    }

}
