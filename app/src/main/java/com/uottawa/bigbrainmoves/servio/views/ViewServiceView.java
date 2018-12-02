package com.uottawa.bigbrainmoves.servio.views;

import com.uottawa.bigbrainmoves.servio.models.ReadOnlyServiceProvider;
import com.uottawa.bigbrainmoves.servio.models.WeeklyAvailabilities;

import java.util.List;

public interface ViewServiceView {
    void displayAvailabilities(WeeklyAvailabilities availabilities);
    void displayServiceProviderInfo(ReadOnlyServiceProvider provider);
    void displayDbError();
    void displayAccountDoesNotExist();
    void displayNoAvailabilitiesOnThisDay();
    void displayAvailableTimes(String startTime, String endTime, List<String> unavailable);
    void displayPrice(double price);
    void displayServiceTypeNoLongerOffered();
    void displayInvalidDate();
    void displayInvalidTime();
    void displayBookingCollision();
    void displayBookingCreated();
    void displayEmptyTime();
}
