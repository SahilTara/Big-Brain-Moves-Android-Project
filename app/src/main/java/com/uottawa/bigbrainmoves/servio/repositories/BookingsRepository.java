package com.uottawa.bigbrainmoves.servio.repositories;

import com.uottawa.bigbrainmoves.servio.models.Booking;
import com.uottawa.bigbrainmoves.servio.models.ReadOnlyService;
import com.uottawa.bigbrainmoves.servio.util.Pair;

import java.util.List;

import io.reactivex.Observable;

public interface BookingsRepository {
    Observable<List<Booking>> getAllBookingsForServiceOnDate(ReadOnlyService service, String date);
    void saveBooking(Booking booking);
    Observable<Pair<Booking, Boolean>> listenForCurrentUserBookingChanges();
}
