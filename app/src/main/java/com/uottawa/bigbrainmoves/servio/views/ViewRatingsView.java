package com.uottawa.bigbrainmoves.servio.views;

import com.uottawa.bigbrainmoves.servio.models.Rating;

public interface ViewRatingsView {
    void displayDataError();
    void displayRatingUpdate(Rating rating, boolean removed);
}
