package com.uottawa.bigbrainmoves.servio.presenters;

import com.uottawa.bigbrainmoves.servio.models.Booking;
import com.uottawa.bigbrainmoves.servio.models.Rating;
import com.uottawa.bigbrainmoves.servio.models.Service;
import com.uottawa.bigbrainmoves.servio.models.WeeklyAvailabilities;
import com.uottawa.bigbrainmoves.servio.repositories.Repository;
import com.uottawa.bigbrainmoves.servio.util.enums.BookingStatus;
import com.uottawa.bigbrainmoves.servio.views.BookingRecyclerView;

import java.util.Calendar;
import java.util.Date;
import java.util.Optional;

import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class BookingRecyclerPresenter {

    private BookingRecyclerView view;
    private Repository repository;

    public BookingRecyclerPresenter(BookingRecyclerView view, Repository repository) {
        this.view = view;
        this.repository = repository;
    }

    public void getService(String serviceName) {

        Observer<Optional<Service>> observer = new Observer<Optional<Service>>() {
            Disposable disposable;

            @Override
            public void onSubscribe(Disposable d) {
                disposable = d;
            }

            // gets account from the repository stream.
            @Override
            public void onNext(Optional<Service> service) {
                if (service.isPresent()) {
                    Service serv = service.get();
                    if (serv.isDisabled() || !serv.isOffered()) {
                        view.displayServiceNotOffered();
                    } else {
                        view.displayService(serv);
                    }
                } else {
                    view.displayServiceNotOffered();
                }
            }

            @Override
            public void onError(Throwable e) {
                view.displayDbError();
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

        repository.getServiceByName(serviceName)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(observer);

    }

    public void updateStatusOfBooking(Booking booking, BookingStatus changedStatus) {
        booking.setStatus(changedStatus);
        repository.saveBooking(booking);
    }

    public void loadRating(Booking booking) {
        String providerUser = booking.getProviderUser();
        String serviceType = booking.getServiceType();
        String rater = booking.getCustomerUser();

        Observer<Optional<Rating>> observer = new Observer<Optional<Rating>>() {
            Disposable disposable;

            @Override
            public void onSubscribe(Disposable d) {
                disposable = d;
            }

            // gets account from the repository stream.
            @Override
            public void onNext(Optional<Rating> rating) {
                Date tempDate = Calendar.getInstance().getTime();
                Rating rate = rating.orElse((new Rating(rater, "", serviceType, providerUser, 0.0, tempDate.getTime())));
                view.displayRating(rate, rating.isPresent());
            }

            @Override
            public void onError(Throwable e) {
                view.displayDbError();
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

        repository.getRating(providerUser, serviceType, rater)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(observer);
    }

    public void setRating(Rating rating, double oldRating, boolean isUpdate) {
        Observer<Boolean> observer = new Observer<Boolean>() {
            Disposable disposable;

            @Override
            public void onSubscribe(Disposable d) {
                disposable = d;
            }

            // gets account from the repository stream.
            @Override
            public void onNext(Boolean result) {
                if (result) {
                    view.displayRated();
                } else {
                    view.displayFailedRating();
                }

            }

            @Override
            public void onError(Throwable e) {
                view.displayDbError();
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

        repository.addRating(rating, oldRating, isUpdate)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(observer);
    }

}
