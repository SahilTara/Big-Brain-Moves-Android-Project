package com.uottawa.bigbrainmoves.servio.repositories;

import com.google.firebase.FirebaseException;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.uottawa.bigbrainmoves.servio.models.Account;
import com.uottawa.bigbrainmoves.servio.models.WeeklyAvailabilities;
import com.uottawa.bigbrainmoves.servio.models.WeeklyAvailabilitiesDBO;
import com.uottawa.bigbrainmoves.servio.util.CurrentAccount;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import androidx.annotation.NonNull;
import io.reactivex.Observable;

public class AvailabilitiesRepositoryFirebase implements  AvailabilitiesRepository {
    private FirebaseDatabase mDatabase = FirebaseDatabase.getInstance();
    private DatabaseReference myRef = mDatabase.getReference();

    private static final String AVAILABILITIES = "availabilities";
    
    @Override
    public void setAvailabilities(WeeklyAvailabilities availabilities) {
        Account account = CurrentAccount.getInstance().getCurrentAccount();
        String username = account.getUsername();

        myRef.child(AVAILABILITIES).child(username).setValue(new WeeklyAvailabilitiesDBO(availabilities));
    }

    @Override
    public Observable<Optional<WeeklyAvailabilities>> getAvailabilities() {
        Account account = CurrentAccount.getInstance().getCurrentAccount();
        String username = account.getUsername();
        return getAvailabilities(username);
    }

    @Override
    public Observable<Optional<WeeklyAvailabilities>> getAvailabilities(String username) {

        return Observable.create(subscriber ->
                myRef.child(AVAILABILITIES).child(username).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()) {
                            WeeklyAvailabilitiesDBO weeklyAvailabilitiesDBO = dataSnapshot.getValue(WeeklyAvailabilitiesDBO.class);
                            WeeklyAvailabilities weeklyAvailabilities = null;

                            if (weeklyAvailabilitiesDBO != null)
                                weeklyAvailabilities = weeklyAvailabilitiesDBO.convertToDomainObject();

                            Optional<WeeklyAvailabilities> availabilities = Optional.ofNullable(weeklyAvailabilities);

                            subscriber.onNext(availabilities);
                            subscriber.onComplete();
                        } else {
                            subscriber.onNext(Optional.empty());
                            subscriber.onComplete();
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        subscriber.onError(new FirebaseException(databaseError.getMessage()));
                    }
                }));
    }

    /**
     * Gets all availabilities for all service providers hashmap form, for fast lookup. Wrapped into
     * an rxjava observable.
     * @return Hashmap containing username as key and availabilities as value in an RxJava Observable.
     */
    public Observable<Map<String, WeeklyAvailabilities>> getAllAvailabilities() {
        return Observable.create(subscriber -> {
            myRef.child(AVAILABILITIES).addListenerForSingleValueEvent(new ValueEventListener() {
                //
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (subscriber.isDisposed())
                        return;

                    // We have some availabilities in the database.
                    if (dataSnapshot.exists()) {
                        HashMap<String, WeeklyAvailabilities> availabilitiesHashMap = new HashMap<>();

                        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                            String username = snapshot.getKey();
                            WeeklyAvailabilitiesDBO availabilitiesDBO = snapshot.getValue(WeeklyAvailabilitiesDBO.class);

                            if (availabilitiesDBO != null) {
                                availabilitiesHashMap.put(username, availabilitiesDBO.convertToDomainObject());
                            }
                        }
                        subscriber.onNext(availabilitiesHashMap);
                        subscriber.onComplete();
                    } else {
                        // deal with no services case
                        subscriber.onNext(new HashMap<>());
                        subscriber.onComplete();
                    }
                }

                @Override // Only really called when the database doesn't give enough permissions.
                public void onCancelled(@NonNull DatabaseError dbError) {
                    if (subscriber.isDisposed())
                        return;
                    subscriber.onError(new FirebaseException(dbError.getMessage()));
                }
            });
        });
    }
}
