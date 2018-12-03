package com.uottawa.bigbrainmoves.servio.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import com.uottawa.bigbrainmoves.servio.R;
import com.uottawa.bigbrainmoves.servio.models.Booking;
import com.uottawa.bigbrainmoves.servio.models.Rating;
import com.uottawa.bigbrainmoves.servio.models.ReadOnlyService;
import com.uottawa.bigbrainmoves.servio.models.Service;
import com.uottawa.bigbrainmoves.servio.presenters.ViewBookingsPresenter;
import com.uottawa.bigbrainmoves.servio.presenters.ViewRatingsPresenter;
import com.uottawa.bigbrainmoves.servio.repositories.DbHandler;
import com.uottawa.bigbrainmoves.servio.repositories.Repository;
import com.uottawa.bigbrainmoves.servio.util.adapters.BookingListAdapter;
import com.uottawa.bigbrainmoves.servio.util.adapters.RatingListAdapter;
import com.uottawa.bigbrainmoves.servio.views.ViewRatingsView;

import java.util.ArrayList;
import java.util.List;

public class ViewRatingsActivity extends AppCompatActivity implements ViewRatingsView {

    private ViewRatingsPresenter presenter;
    private final Repository repository = new DbHandler();
    private List<Rating> listOfRatings = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_ratings);
        presenter = new ViewRatingsPresenter(this,  repository);
        Intent intent = getIntent();
        ReadOnlyService service = intent.getParcelableExtra("service");
        presenter.listenForRatingUpdates(service);
    }

    public void displayDataError() {
        Toast.makeText(getApplicationContext(),
                "Unable to retrieve booking types list due to insufficient permissions.",
                Toast.LENGTH_LONG).show();
    }

    public void displayRatingUpdate(Rating rating, boolean removed) {
        listOfRatings.remove(rating);
        if (!removed)
            listOfRatings.add(rating);

        RecyclerView recyclerView = findViewById(R.id.ratingRecyclerView);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setHasFixedSize(true);
        RecyclerView.Adapter adapter  = new RatingListAdapter(listOfRatings);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(layoutManager);
    }
}
