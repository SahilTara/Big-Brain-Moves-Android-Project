package com.uottawa.bigbrainmoves.servio.presenters;

import com.uottawa.bigbrainmoves.servio.models.Service;
import com.uottawa.bigbrainmoves.servio.models.ServiceType;
import com.uottawa.bigbrainmoves.servio.models.WeeklyAvailabilities;
import com.uottawa.bigbrainmoves.servio.repositories.Repository;
import com.uottawa.bigbrainmoves.servio.util.DayOfWeek;
import com.uottawa.bigbrainmoves.servio.util.Pair;
import com.uottawa.bigbrainmoves.servio.views.FindServicesView;

import java.util.HashMap;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;

public class FindServicesPresenter {
    private final FindServicesView view;
    private final Repository repository;
    private double rating = 0;
    private String time = "00:00";
    private String filterString = ".*()";
    private boolean filterByTime = false;
    private DayOfWeek dayOfWeek = DayOfWeek.ANY;

    public FindServicesPresenter(FindServicesView view, Repository repository) {
        this.view = view;
        this.repository = repository;
    }

    public void listenForServiceTypeChanges() {
        Observer<Pair<ServiceType, Boolean>> resultObserver = new Observer<Pair<ServiceType, Boolean>>() {
            Disposable disposable;

            @Override
            public void onSubscribe(Disposable d) {
                disposable = d;
            }

            @Override
            public void onNext(Pair<ServiceType, Boolean> result) {
                view.displayLiveServicesInSearch(result.first, result.second);
            }

            @Override
            public void onError(Throwable e) {
                // want silence for this one.
                //TODO: Remove after debugging.
                e.printStackTrace();
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
        repository.listenForServiceTypeChanges().subscribe(resultObserver);
    }

    public void listenForServiceChanges() {
        Observer<Pair<Service, Boolean>> resultObserver = new Observer<Pair<Service, Boolean>>() {
            Disposable disposable;

            @Override
            public void onSubscribe(Disposable d) {
                disposable = d;
            }

            @Override
            public void onNext(Pair<Service, Boolean> result) {
                view.displayLiveServices(result.first, result.second);
            }

            @Override
            public void onError(Throwable e) {
                // want silence for this one.
                //TODO: Remove after debugging.
                e.printStackTrace();
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

        repository.listenForServiceChanges().subscribe(resultObserver);
    }

    public void filterList(List<Service> services) {


        Observer<HashMap<String, WeeklyAvailabilities>> hashmapObserver =
                new Observer<HashMap<String, WeeklyAvailabilities>>() {
            Disposable disposable;

            @Override
            public void onSubscribe(Disposable d) {
                disposable = d;
            }

            @Override
            public void onNext(HashMap<String, WeeklyAvailabilities> hashMap) {
                Observer<List<Service>> serviceObserver = new Observer<List<Service>>() {
                    Disposable disposable;

                    @Override
                    public void onSubscribe(Disposable d) {
                        disposable = d;
                    }

                    @Override
                    public void onNext(List<Service> serviceList) {
                        view.displayFiltered(serviceList);
                    }

                    @Override
                    public void onError(Throwable e) {
                        // want silence for this one.
                        //TODO: Remove after debugging.
                        e.printStackTrace();
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

                Observable.fromIterable(services).filter(service -> ((service.getRating() >= rating) &&
                        service.getType().matches(filterString) &&
                        checkDayOfWeekAvailable(dayOfWeek, hashMap.get(service.getServiceProviderUser()))
                )).toList().toObservable().subscribe(serviceObserver);

            }

            @Override
            public void onError(Throwable e) {
                // want silence for this one.
                //TODO: Remove after debugging.
                e.printStackTrace();
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

        repository.getAllAvailabilities().subscribe(hashmapObserver);
    }

    private boolean checkDayOfWeekAvailable(DayOfWeek dayOfWeek, WeeklyAvailabilities availabilities) {
        if (availabilities == null) return false;
        String mondayStart = availabilities.getMondayStart();
        String mondayEnd = availabilities.getMondayEnd();

        boolean isAvailableMonday =  ((dayOfWeek.equals(DayOfWeek.MONDAY) || dayOfWeek.equals(DayOfWeek.ANY))
                && ((time.compareTo(mondayStart) >= 0 // check if >= than start
                && time.compareTo(mondayEnd) < 0) || !filterByTime) // less than end
                && mondayStart.matches("\\d{2}:\\d{2}"));

        String tuesdayStart = availabilities.getTuesdayStart();
        String tuesdayEnd = availabilities.getTuesdayEnd();

        boolean isAvailableTuesday =  ((dayOfWeek.equals(DayOfWeek.TUESDAY) || dayOfWeek.equals(DayOfWeek.ANY))
                && ((time.compareTo(tuesdayStart) >= 0 // check if >= than start
                && time.compareTo(tuesdayEnd) < 0) || !filterByTime) // less than end
                && tuesdayStart.matches("\\d{2}:\\d{2}"));

        String wednesdayStart = availabilities.getWednesdayStart();
        String wednesdayEnd = availabilities.getWednesdayEnd();

        boolean isAvailableWednesday =  ((dayOfWeek.equals(DayOfWeek.WEDNESDAY) || dayOfWeek.equals(DayOfWeek.ANY))
                && ((time.compareTo(wednesdayStart) >= 0 // check if >= than start
                && time.compareTo(wednesdayEnd) < 0) || !filterByTime) // less than end
                && wednesdayStart.matches("\\d{2}:\\d{2}"));

        String thursdayStart = availabilities.getThursdayStart();
        String thursdayEnd = availabilities.getThursdayEnd();

        boolean isAvailableThursday =  ((dayOfWeek.equals(DayOfWeek.THURSDAY) || dayOfWeek.equals(DayOfWeek.ANY))
                && ((time.compareTo(thursdayStart) >= 0 // check if >= than start
                && time.compareTo(thursdayEnd) < 0) || !filterByTime) // less than end
                && thursdayStart.matches("\\d{2}:\\d{2}"));

        String fridayStart = availabilities.getFridayStart();
        String fridayEnd = availabilities.getFridayEnd();

        boolean isAvailableFriday =  ((dayOfWeek.equals(DayOfWeek.FRIDAY) || dayOfWeek.equals(DayOfWeek.ANY))
                && ((time.compareTo(fridayStart) >= 0 // check if >= than start
                && time.compareTo(fridayEnd) < 0) || !filterByTime) // less than end
                && fridayStart.matches("\\d{2}:\\d{2}"));


        String saturdayStart = availabilities.getSaturdayStart();
        String saturdayEnd = availabilities.getSaturdayEnd();

        boolean isAvailableSaturday =  ((dayOfWeek.equals(DayOfWeek.SATURDAY) || dayOfWeek.equals(DayOfWeek.ANY))
                && ((time.compareTo(saturdayStart) >= 0 // check if >= than start
                && time.compareTo(saturdayEnd) < 0) || !filterByTime) // less than end
                && saturdayStart.matches("\\d{2}:\\d{2}"));

        String sundayStart = availabilities.getSundayStart();
        String sundayEnd = availabilities.getSundayEnd();

        boolean isAvailableSunday =  ((dayOfWeek.equals(DayOfWeek.SUNDAY) || dayOfWeek.equals(DayOfWeek.ANY))
                && ((time.compareTo(sundayStart) >= 0 // check if >= than start
                && time.compareTo(sundayEnd) < 0) || !filterByTime) // less than end
                && sundayStart.matches("\\d{2}:\\d{2}"));

        return isAvailableMonday || isAvailableTuesday || isAvailableWednesday || isAvailableThursday ||
                isAvailableFriday || isAvailableSaturday || isAvailableSunday;
    }

    public void setExtraFilters(List<Service> services, double rating, boolean filterByTime, String time, DayOfWeek dayOfWeek) {
        this.rating = rating;
        this.filterByTime = filterByTime;
        this.time = time;
        this.dayOfWeek = dayOfWeek;
        filterList(services);
    }

    public void addToFilterString(List<Service> services, String toBeAdded) {
        if (filterString.equals(".*()"))
            filterString = filterString.replace(")", toBeAdded + ")");
        else
            filterString = filterString.replace(")",  "|" + toBeAdded + ")");
        filterList(services);
    }

    public void removeFromFilterString(List<Service> services, String toBeRemoved) {
        if (filterString.equals(".*(" + toBeRemoved + ")"))
            filterString = ".*()";
        else {

            String replaced = filterString.replace( "|" + toBeRemoved, "");
            if (replaced.equals(filterString)) // is first string
                filterString = filterString.replace(toBeRemoved + "|", "");
            else // is any other string.
                filterString = replaced;
        }
        filterList(services);
    }


}
