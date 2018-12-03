package com.uottawa.bigbrainmoves.servio.views;

import com.uottawa.bigbrainmoves.servio.models.Booking;

public interface ViewBookingsView {
    void displayDataError();
    void displayBookingUpdate(Booking serviceType, boolean removed);
}
