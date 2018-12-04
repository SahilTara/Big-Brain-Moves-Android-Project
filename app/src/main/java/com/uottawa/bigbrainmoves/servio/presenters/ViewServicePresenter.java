package com.uottawa.bigbrainmoves.servio.presenters;


import com.uottawa.bigbrainmoves.servio.models.Account;
import com.uottawa.bigbrainmoves.servio.models.Booking;
import com.uottawa.bigbrainmoves.servio.models.ReadOnlyService;
import com.uottawa.bigbrainmoves.servio.models.ReadOnlyServiceProvider;
import com.uottawa.bigbrainmoves.servio.models.ServiceProvider;
import com.uottawa.bigbrainmoves.servio.models.ServiceType;
import com.uottawa.bigbrainmoves.servio.models.WeeklyAvailabilities;
import com.uottawa.bigbrainmoves.servio.repositories.Repository;
import com.uottawa.bigbrainmoves.servio.util.CurrentAccount;
import com.uottawa.bigbrainmoves.servio.util.enums.BookingStatus;
import com.uottawa.bigbrainmoves.servio.views.ViewServiceView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.Set;

import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class ViewServicePresenter {

    private final Repository repository;
    private final ViewServiceView view;

    public ViewServicePresenter(Repository repository, ViewServiceView view) {
        this.repository = repository;
        this.view = view;
    }

    public void loadServiceProviderForService(ReadOnlyService service) {
        String username = service.getServiceProviderUser();

        Observer<Optional<ServiceProvider>> observer = new Observer<Optional<ServiceProvider>>() {
            Disposable disposable;

            @Override
            public void onSubscribe(Disposable d) {
                disposable = d;
            }

            // gets account from the repository stream.
            @Override
            public void onNext(Optional<ServiceProvider> serviceProviderOptional) {
                if (serviceProviderOptional.isPresent()) {
                    ReadOnlyServiceProvider serviceProvider = serviceProviderOptional.get();
                    // loads the gotten availabilities or creates a default if none exist.
                    view.displayServiceProviderInfo(serviceProvider);
                } else {
                    view.displayAccountDoesNotExist();
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
        repository.getServiceProviderFromDatabase(username)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(observer);
    }

    public void loadAvailabilitiesForService(ReadOnlyService service) {
        String username = service.getServiceProviderUser();

        Observer<Optional<WeeklyAvailabilities>> observer = new Observer<Optional<WeeklyAvailabilities>>() {
            Disposable disposable;

            @Override
            public void onSubscribe(Disposable d) {
                disposable = d;
            }

            // gets account from the repository stream.
            @Override
            public void onNext(Optional<WeeklyAvailabilities> availabilities) {
                // loads the gotten availabilities or creates a default if none exist.
                view.displayAvailabilities(availabilities.orElseGet(WeeklyAvailabilities::new));
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

        repository.getAvailabilities(username)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(observer);
    }

    public void getServicePrice(ReadOnlyService service, double duration) {
        Observer<Optional<ServiceType>> observer = new Observer<Optional<ServiceType>>() {
            Disposable disposable;

            @Override
            public void onSubscribe(Disposable d) {
                disposable = d;
            }

            // gets account from the repository stream.
            @Override
            public void onNext(Optional<ServiceType> serviceTypeOptional) {
                if (serviceTypeOptional.isPresent()) {
                    ServiceType serviceType = serviceTypeOptional.get();
                    view.displayPrice(serviceType.getRate() * duration);
                } else {
                    view.displayServiceTypeNoLongerOffered();
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
        repository.getServiceType(service.getType()).subscribe(observer);
    }


    public void getTimeRestrictions(ReadOnlyService service, Calendar date, WeeklyAvailabilities availabilities) {


        String startTime = "";
        String endTime = "";
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);
        String dateRepresentation = formatter.format(date.getTime());

        switch(date.get(Calendar.DAY_OF_WEEK)) {
            case Calendar.MONDAY:
                startTime = availabilities.getMondayStart();
                endTime = availabilities.getMondayEnd();
                break;
            case Calendar.TUESDAY:
                startTime = availabilities.getTuesdayStart();
                endTime = availabilities.getTuesdayEnd();
                break;
            case Calendar.WEDNESDAY:
                startTime = availabilities.getWednesdayStart();
                endTime = availabilities.getWednesdayEnd();
                break;
            case Calendar.THURSDAY:
                startTime = availabilities.getThursdayStart();
                endTime = availabilities.getThursdayEnd();
                break;
            case Calendar.FRIDAY:
                startTime = availabilities.getFridayStart();
                endTime = availabilities.getFridayEnd();
                break;
            case Calendar.SATURDAY:
                startTime = availabilities.getSaturdayStart();
                endTime = availabilities.getSaturdayEnd();
                break;
            case Calendar.SUNDAY:
                startTime = availabilities.getSundayStart();
                endTime = availabilities.getSundayEnd();
                break;
            default:
                // We only consider monday-sunday as days.
        }

        // Rounds to nearest thirty minutes
        Calendar now = Calendar.getInstance();
        int minutesToThirty = 30 - now.get(Calendar.MINUTE) % 30;
        now.add(Calendar.MINUTE, minutesToThirty);

        // deals with time logic to see if the person can book.
        SimpleDateFormat format = new SimpleDateFormat("HH:mm", Locale.ENGLISH);
        String timeNow = format.format(now.getTime());

        boolean isSameDay = dateRepresentation.equals(formatter.format(now.getTime()));

        if (!startTime.contains(":") || !endTime.contains(":") || timeNow.compareTo(endTime) >= 0 && isSameDay) {
            view.displayNoAvailabilitiesOnThisDay();
            return;
        } else if (timeNow.compareTo(startTime) >= 0 && isSameDay) {
            startTime = timeNow;
        }

        final String startingTime = startTime;
        final String endingTime = endTime;

        Observer<List<Booking>> bookingObserver = new Observer<List<Booking>>() {
                Disposable disposable;

                @Override
                public void onSubscribe(Disposable d) {
                    disposable = d;
                }

                // gets account from the repository stream.
                @Override
                public void onNext(List<Booking> bookings) {
                    List<String> takenTimes = new ArrayList<>();
                    Set<String> times = new HashSet<>(
                        Arrays.asList("00:00","00:30","01:00","01:30","02:00","02:30",
                                      "03:00","03:30","04:00","04:30","05:00","05:30",
                                      "06:00","06:30","07:00","07:30","08:00","08:30",
                                      "09:00","09:30","10:00","10:30","11:00","11:30",
                                      "12:00","12:30","13:00","13:30","14:00","14:30",
                                      "15:00","15:30","16:00","16:30","17:00","17:30",
                                      "18:00","18:30","19:00","19:30","20:00","20:30",
                                      "21:00","21:30","22:00","22:30","23:00","23:30")
                    );


                    for (Booking booking : bookings) {
                        String start = booking.getStartTime();
                        String end = booking.getEndTime();
                        Iterator<String> it = times.iterator();
                        // removes items from time set as they are found and adds them to the takenTimes.
                        while (it.hasNext()) {
                            String time = it.next();
                            if (time.compareTo(start) >= 0 && time.compareTo(end) <= 0) {
                                it.remove();
                                takenTimes.add(time);
                            }
                        }
                    }

                    view.displayAvailableTimes(startingTime, endingTime, takenTimes);
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

        repository.getAllBookingsForServiceOnDate(service, dateRepresentation).subscribe(bookingObserver);
    }

    public void createBooking(ReadOnlyService service, String date, String timeRange, double price) {
        if (!date.contains("-")) {
            view.displayInvalidDate();
            return;
        } else if (!timeRange.contains(":")) {
            view.displayEmptyTime();
            return;
        }

        String[] beginAndEnd = timeRange.split("~");
        String startingTime  = beginAndEnd[0];
        String endingTime = beginAndEnd[1];

        if (startingTime.equals(endingTime)) {
            view.displayInvalidTime();
            return;
        }

        Account account = CurrentAccount.getInstance().getCurrentAccount();
        String customer = account.getUsername();
        String provider = service.getServiceProviderUser();
        String serviceType = service.getType();

        Observer<List<Booking>> bookingObserver = new Observer<List<Booking>>() {
            Disposable disposable;

            @Override
            public void onSubscribe(Disposable d) {
                disposable = d;
            }

            // gets account from the repository stream.
            @Override
            public void onNext(List<Booking> bookings) {

                for (Booking booking : bookings) {
                    String start = booking.getStartTime();
                    String end = booking.getEndTime();

                    if (!(endingTime.compareTo(start) < 0 || startingTime.compareTo(end) > 0)) {
                        view.displayBookingCollision();
                        return;
                    }
                }


                repository.saveBooking(new Booking(provider, customer, serviceType,
                        date, startingTime, endingTime, price, BookingStatus.PENDING));
                view.displayBookingCreated();
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

        repository.getAllBookingsForServiceOnDate(service, date).subscribe(bookingObserver);

    }
}
