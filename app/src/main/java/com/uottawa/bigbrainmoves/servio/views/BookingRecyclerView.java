package com.uottawa.bigbrainmoves.servio.views;


import com.uottawa.bigbrainmoves.servio.models.Rating;
import com.uottawa.bigbrainmoves.servio.models.Service;

public interface BookingRecyclerView {
    void displayRated();
    void displayFailedRating();
    void displayDbError();
    void displayService(Service service);
    void displayServiceNotOffered();
    void displayRating(Rating rating, boolean isRated);
}
