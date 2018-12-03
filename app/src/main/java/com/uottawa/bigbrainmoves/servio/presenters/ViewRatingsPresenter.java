package com.uottawa.bigbrainmoves.servio.presenters;

import com.uottawa.bigbrainmoves.servio.models.Booking;
import com.uottawa.bigbrainmoves.servio.models.Rating;
import com.uottawa.bigbrainmoves.servio.models.ReadOnlyService;
import com.uottawa.bigbrainmoves.servio.repositories.Repository;
import com.uottawa.bigbrainmoves.servio.util.Pair;
import com.uottawa.bigbrainmoves.servio.views.ViewBookingsView;
import com.uottawa.bigbrainmoves.servio.views.ViewRatingsView;

import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class ViewRatingsPresenter {
    private ViewRatingsView view;
    private Repository repository;

    public ViewRatingsPresenter(ViewRatingsView view, Repository repository) {
        this.view = view;
        this.repository = repository;
    }

    public void listenForRatingUpdates(ReadOnlyService service) {
        Observer<Pair<Rating, Boolean>> resultObserver = new Observer<Pair<Rating, Boolean>>() {
            Disposable disposable;

            @Override
            public void onSubscribe(Disposable d) {
                disposable = d;
            }

            @Override
            public void onNext(Pair<Rating, Boolean> result) {
                view.displayRatingUpdate(result.first, result.second);
            }

            @Override
            public void onError(Throwable e) {
                // want silence for this one.
                disposable.dispose();
                disposable = null;
            }

            // do some cleanup
            @Override
            public void onComplete() {
                disposable.dispose();
                disposable = null;
            }
        };

        repository.listenForRatingChanges(service)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(resultObserver);
    }
}
