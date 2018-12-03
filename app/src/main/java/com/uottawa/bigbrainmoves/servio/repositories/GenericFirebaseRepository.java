package com.uottawa.bigbrainmoves.servio.repositories;

import com.google.firebase.database.DataSnapshot;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.ObservableEmitter;
import io.reactivex.functions.BiConsumer;
import io.reactivex.functions.Consumer;

class GenericFirebaseRepository {
    //TODO Rework comments here.
    /**
     * Generic on data change method for getting all instances of a certain type from the database.
     * @param dataSnapshot firebase datasnapshot of the results where the instances live.
     * @param subscriber the rxjava emitter.
     * @param <K> The type of the data.
     */
    <K> void getAllGenericOnDataChange(DataSnapshot dataSnapshot,
                                       ObservableEmitter<List<K>> subscriber,
                                       Class<K> classToReturn) {


        // default call to the main getAllGenericOnDataChange method.
        getAllGenericOnDataChange(dataSnapshot, subscriber, classToReturn, List::add, (elements, subber) -> {
            subber.onNext(elements);
            subber.onComplete();
        }, (subber) -> {
            subber.onNext(new ArrayList<>());
            subber.onComplete();
        });
    }

    // Used to allow for less code duplication.
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
}
