package com.uottawa.bigbrainmoves.servio.repositories;

import com.google.firebase.FirebaseException;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.uottawa.bigbrainmoves.servio.models.Account;
import com.uottawa.bigbrainmoves.servio.models.Booking;
import com.uottawa.bigbrainmoves.servio.models.ReadOnlyService;
import com.uottawa.bigbrainmoves.servio.models.ServiceType;
import com.uottawa.bigbrainmoves.servio.models.WeeklyAvailabilities;
import com.uottawa.bigbrainmoves.servio.util.CurrentAccount;
import com.uottawa.bigbrainmoves.servio.util.Pair;
import com.uottawa.bigbrainmoves.servio.util.enums.AccountType;
import com.uottawa.bigbrainmoves.servio.util.enums.DayOfWeek;

import java.util.HashMap;
import java.util.List;
import java.util.Optional;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import io.reactivex.Observable;

public class BookingsRepositoryFirebase extends GenericFirebaseRepository implements  BookingsRepository {
    private FirebaseDatabase mDatabase = FirebaseDatabase.getInstance();
    private DatabaseReference myRef = mDatabase.getReference();
    private static final String BOOKINGS = "bookings";

    public Observable<List<Booking>> getAllBookingsForServiceOnDate(ReadOnlyService service, String date) {
        String provider = service.getServiceProviderUser();
        String type = service.getType();
        return Observable.create(subscriber ->
                myRef.child(BOOKINGS).orderByChild("providerServiceTypeParsableYearMonthDay")
                        .equalTo(provider + type + String.valueOf(true) + date).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        getAllGenericOnDataChange(dataSnapshot, subscriber, Booking.class);
                    }

                    @Override // Only really called when the database doesn't give enough permissions.
                    public void onCancelled(@NonNull DatabaseError dbError) {
                        if (subscriber.isDisposed())
                            return;
                        subscriber.onError(new FirebaseException(dbError.getMessage()));
                    }
                }));

    }

    public Observable<Pair<Booking, Boolean>> listenForCurrentUserBookingChanges() {
        Account account = CurrentAccount.getInstance().getCurrentAccount();
        String child;
        if (account.getType().equals(AccountType.HOME_OWNER)) {
            child = "customerUser";
        } else {
            child = "providerUser";
        }

        Query ref = myRef.child(BOOKINGS);

        if (!account.getType().equals(AccountType.ADMIN)) {
            ref = ref.orderByChild(child).equalTo(account.getUsername());
        }

        final Query myQuery = ref;

        return Observable.create(subscriber ->
                myQuery.addChildEventListener(getGenericChildEventListener(Booking.class, subscriber)));
    }

    public void saveBooking(Booking booking) {
        HashMap<String, Object> updateMap = new HashMap<>();
        updateMap.put(BOOKINGS + "/" + booking.getProviderServiceTypeParsableYearMonthDayCustomer(), booking);

        if (!booking.getStatus().isParsable()) {
            String key = booking.getProviderServiceTypeParsableYearMonthDayCustomer().replace("false", "true");
            updateMap.put(BOOKINGS + "/" + key, null);
        }

        myRef.updateChildren(updateMap);

    }
}
