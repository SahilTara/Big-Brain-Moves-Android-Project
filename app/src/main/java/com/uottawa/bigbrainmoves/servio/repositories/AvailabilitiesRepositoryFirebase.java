package com.uottawa.bigbrainmoves.servio.repositories;

import com.google.firebase.FirebaseException;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.uottawa.bigbrainmoves.servio.models.Account;
import com.uottawa.bigbrainmoves.servio.models.WeeklyAvailabilities;
import com.uottawa.bigbrainmoves.servio.util.CurrentAccount;

import java.util.Optional;

import io.reactivex.Observable;

public class AvailabilitiesRepositoryFirebase implements  AvailabilitiesRepository {
    private FirebaseDatabase mDatabase = FirebaseDatabase.getInstance();
    private DatabaseReference myRef = mDatabase.getReference();

    @Override
    public void setAvailabilities(WeeklyAvailabilities availabilities) {
        Account account = CurrentAccount.getInstance().getCurrentAccount();
        String username = account.getUsername();

        myRef.child("availabilities").child(username).setValue(availabilities);
    }

    @Override
    public Observable<Optional<WeeklyAvailabilities>> getAvailabilities() {
        Account account = CurrentAccount.getInstance().getCurrentAccount();
        String username = account.getUsername();
        return Observable.create(subscriber -> {
            myRef.child("availabilities").child(username).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        Optional<WeeklyAvailabilities> availabilities = Optional
                                .ofNullable(dataSnapshot.getValue(WeeklyAvailabilities.class));

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
            });
        });
    }
}
