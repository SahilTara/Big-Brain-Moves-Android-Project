package com.uottawa.bigbrainmoves.servio.presenters;

import com.uottawa.bigbrainmoves.servio.models.ServiceProvider;
import com.uottawa.bigbrainmoves.servio.models.WeeklyAvailabilities;
import com.uottawa.bigbrainmoves.servio.repositories.Repository;
import com.uottawa.bigbrainmoves.servio.util.CurrentAccount;
import com.uottawa.bigbrainmoves.servio.util.DayOfWeek;
import com.uottawa.bigbrainmoves.servio.util.Pair;
import com.uottawa.bigbrainmoves.servio.views.ManageAvailabilitiesView;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Optional;

import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;

public class ManageAvailabilitiesPresenter {


    private final ManageAvailabilitiesView view;
    private final Repository repository;

    public ManageAvailabilitiesPresenter(ManageAvailabilitiesView view, Repository repository) {
        this.view = view;
        this.repository = repository;
    }


    public void getAvailabilities() {
        ServiceProvider provider = (ServiceProvider) CurrentAccount.getInstance().getCurrentAccount();
        // Simple observer set up.
        Observer<Optional<WeeklyAvailabilities>> observer = new Observer<Optional<WeeklyAvailabilities>>() {
            Disposable disposable;

            @Override
            public void onSubscribe(Disposable d) {
                disposable = d;
            }

            // gets account from the repository stream.
            @Override
            public void onNext(Optional<WeeklyAvailabilities> availabilities) {
                if (!availabilities.isPresent()) { // no availabilities
                    provider.setAvailabilities(new WeeklyAvailabilities());
                } else { // we have availabilities valid!
                    provider.setAvailabilities(availabilities.get());
                }
                WeeklyAvailabilities weeklyAvailabilities = provider.getAvailabilities();


                view.displayTimes(weeklyAvailabilities);
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
        repository.getAvailabilities().subscribe(observer);
    }


    /**
     * Given an id we extract the time we must set in the weekly availabilities object.
     * @param time the time to set to.
     * @param timeSlot the timeSlot of the clicked object.
     */
    public void setTime(String time, WeeklyAvailabilities.TimeSlot timeSlot) {
        ServiceProvider provider = (ServiceProvider) CurrentAccount.getInstance().getCurrentAccount();
        WeeklyAvailabilities weeklyAvailabilities = provider.getAvailabilities();

        try {
            // Essentially invokes the right setter by using reflections
            Method setter = weeklyAvailabilities.getClass().getMethod(timeSlot.getMethodName(), String.class);
            setter.invoke(weeklyAvailabilities, time);
        } catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
            //TODO: DONT SWALLOW LATER!.
        }
    }

    public void saveTimes() {
        ServiceProvider provider = (ServiceProvider) CurrentAccount.getInstance().getCurrentAccount();
        WeeklyAvailabilities weeklyAvailabilities = provider.getAvailabilities();
        Optional<DayOfWeek> invalidDay = weeklyAvailabilities.getInvalidTimeSlot();

        if(invalidDay.isPresent()) {
            view.displayDayInvalid(invalidDay.get());
        } else {
            repository.setAvailabilities(weeklyAvailabilities);
            view.displaySuccessfulSave();
        }
    }


    /**
     * gets the start or end time restriction of the day depending on the id passed.
     * @param id
     * @return a Pair containing the time as a string and whether it or not it is a start restriction.
     */
    public Pair<String, Boolean> getTimeRestriction(String id) {
        ServiceProvider provider = (ServiceProvider) CurrentAccount.getInstance().getCurrentAccount();
        WeeklyAvailabilities weeklyAvailabilities = provider.getAvailabilities();
        String time = "";
        String compare = id;
        boolean isStart = !compare.toLowerCase().contains("start");

        switch (compare) {
            case "MONDAY_START":
                time = weeklyAvailabilities.getMondayEnd();
                if (time.equals("End Time"))
                    time = "23:30";
                break;
            case "MONDAY_END":
                time = weeklyAvailabilities.getMondayStart();
                if (time.equals("Start Time"))
                    time = "00:00";
                break;
            case "TUESDAY_START":
                time = weeklyAvailabilities.getTuesdayEnd();
                if (time.equals("End Time"))
                    time = "23:30";
                break;
            case "TUESDAY_END":
                time = weeklyAvailabilities.getTuesdayStart();
                if (time.equals("Start Time"))
                    time = "00:00";
                break;
            case "WEDNESDAY_START":
                time = weeklyAvailabilities.getWednesdayEnd();
                if (time.equals("End Time"))
                    time = "23:30";
                break;
            case "WEDNESDAY_END":
                time = weeklyAvailabilities.getWednesdayStart();
                if (time.equals("Start Time"))
                    time = "00:00";
                break;
            case "THURSDAY_START":
                time = weeklyAvailabilities.getThursdayEnd();
                if (time.equals("End Time"))
                    time = "23:30";
                break;
            case "THURSDAY_END":
                time = weeklyAvailabilities.getThursdayStart();
                if (time.equals("Start Time"))
                    time = "00:00";
                break;
            case "FRIDAY_START":
                time = weeklyAvailabilities.getFridayEnd();
                if (time.equals("End Time"))
                    time = "23:30";
                break;
            case "FRIDAY_END":
                time = weeklyAvailabilities.getFridayStart();
                if (time.equals("Start Time"))
                    time = "00:00";
                break;
            case "SATURDAY_START":
                time = weeklyAvailabilities.getSaturdayEnd();
                if (time.equals("End Time"))
                    time = "23:30";
                break;
            case "SATURDAY_END":
                time = weeklyAvailabilities.getSaturdayStart();
                if (time.equals("Start Time"))
                    time = "00:00";
                break;
            case "SUNDAY_START":
                time = weeklyAvailabilities.getSundayEnd();
                if (time.equals("End Time"))
                    time = "23:30";
                break;
            case "SUNDAY_END":
                time = weeklyAvailabilities.getSundayStart();
                if (time.equals("Start Time"))
                    time = "00:00";
                break;
        }

        return new Pair<>(time, isStart);
    }

}
