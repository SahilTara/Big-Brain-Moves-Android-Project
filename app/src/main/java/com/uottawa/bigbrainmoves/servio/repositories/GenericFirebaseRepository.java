package com.uottawa.bigbrainmoves.servio.repositories;

import com.google.firebase.FirebaseException;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.uottawa.bigbrainmoves.servio.util.Pair;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import io.reactivex.ObservableEmitter;
import io.reactivex.functions.BiConsumer;
import io.reactivex.functions.Consumer;

class GenericFirebaseRepository {

    /**
     * Generic on data change method for getting all instances of a certain type from the database.
     * This method calls the hook method getAllGenericOnDataChange with default implementations of the
     * methods.
     * @param dataSnapshot firebase datasnapshot of the results where the instances live.
     * @param subscriber the rxjava emitter.
     * @param classToReturn the class of the type you wish to return.
     * @param <K> The type of the data.
     */
    <K> void getAllGenericOnDataChange(DataSnapshot dataSnapshot,
                                       ObservableEmitter<List<K>> subscriber,
                                       Class<K> classToReturn) {


        // default call to the main getAllGenericOnDataChange method.
        getAllGenericOnDataChange(dataSnapshot, subscriber, classToReturn, List::add, (elements, subber) -> {
            subber.onNext(elements);
            subber.onComplete();
        }, subber -> {
            subber.onNext(new ArrayList<>());
            subber.onComplete();
        });
    }

    /**
     * Generic on data change method for getting all instances of a certain type from the database.
     * This method allows the addition of hook callbacks to customize execution.
     *
     * @param dataSnapshot firebase datasnapshot of the results where the instances live.
     * @param subscriber the rxjava emitter.
     * @param classTypeToReturn the class of the type you wish to return.
     * @param mutator the mutating callback, gets called within the loop if the item exists and isn't null,
     *                we usually add items to the list here.
     * @param postLoopAction the postLoopAction callback which is called after all items in the database
     *                       have been added to the list.
     * @param snapshotDoesNotExist the callback called when no instances of the type exist.
     *                             Usually return an empty list of some kind.
     * @param <K> The type of the data.
     */
    <K> void getAllGenericOnDataChange(DataSnapshot dataSnapshot,
                                               ObservableEmitter<List<K>> subscriber,
                                               Class<K> classTypeToReturn,
                                               BiConsumer<List<K>, K> mutator,
                                               BiConsumer<List<K>, ObservableEmitter<List<K>>> postLoopAction,
                                               Consumer<ObservableEmitter<List<K>>> snapshotDoesNotExist) {

        if (subscriber.isDisposed())
            return;


        // We have some elements in the database
        if (dataSnapshot.exists()) {
            ArrayList<K> elements = new ArrayList<>();
            for (DataSnapshot snapshot : dataSnapshot.getChildren()) {

                K element = snapshot.getValue(classTypeToReturn);
                if (element != null) {
                    try {
                        // execute our mutator hook method.
                        mutator.accept(elements, element);
                    } catch (Exception e) {
                        // Can't really do anything else since exception could be anything
                        subscriber.onError(e);
                    }
                }
            }

            try {
                // call the post action hook.
                postLoopAction.accept(elements, subscriber);
            } catch (Exception e) {
                // Can't really do anything else since exception could be anything
                subscriber.onError(e);
            }

        } else {
            // deal with no elements case by calling the snapshotDoesNotExist hook.
            try {
                snapshotDoesNotExist.accept(subscriber);
            } catch (Exception e) {
                // Cannot do anything here
                subscriber.onError(e);
            }
        }


    }

    <K> ChildEventListener getGenericChildEventListener(Class<K> classToGet, ObservableEmitter<Pair<K, Boolean>> subscriber) {
        return new ChildEventListener() {
            /**
             * This method is used to generalize all of the different child listener events.
             * The only difference in implementation is the isRemoved state for all of them so,
             * the code is centralized here.
             * @param dataSnapshot the FireBase DataSnapshot of the item's location.
             * @param isRemoved whether or not the item was removed.
             */
            private void doListenEvent(DataSnapshot dataSnapshot, boolean isRemoved) {
                K item = dataSnapshot.getValue(classToGet);
                subscriber.onNext(new Pair<>(item, isRemoved));
            }

            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                doListenEvent(dataSnapshot, false);
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                doListenEvent(dataSnapshot, false);
            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {
                doListenEvent(dataSnapshot, true);
            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                /* We don't handle onChildMoved since we don't move children around in our database.
                 * We only simply add remove and change state.
                 */
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                subscriber.onError(new FirebaseException(databaseError.getMessage()));
            }
        };
    }
}
