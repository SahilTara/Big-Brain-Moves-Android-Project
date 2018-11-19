package com.uottawa.bigbrainmoves.servio.presenters;

import com.uottawa.bigbrainmoves.servio.models.Account;
import com.uottawa.bigbrainmoves.servio.models.ServiceProvider;
import com.uottawa.bigbrainmoves.servio.models.WeeklyAvailabilities;
import com.uottawa.bigbrainmoves.servio.repositories.Repository;
import com.uottawa.bigbrainmoves.servio.util.CurrentAccount;
import com.uottawa.bigbrainmoves.servio.util.DayOfWeek;
import com.uottawa.bigbrainmoves.servio.util.Pair;
import com.uottawa.bigbrainmoves.servio.views.ManageAvailabilitiesView;

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

                // Monday Init
                String mondayStart = weeklyAvailabilities.getMondayStart();
                if (mondayStart.equals(""))
                    mondayStart = "Start Time";

                String mondayEnd = weeklyAvailabilities.getMondayEnd();
                if (mondayEnd.equals(""))
                    mondayEnd = "End Time";

                // Tuesday Init
                String tuesdayStart = weeklyAvailabilities.getTuesdayStart();
                if (tuesdayStart.equals(""))
                    tuesdayStart = "Start Time";

                String tuesdayEnd = weeklyAvailabilities.getTuesdayEnd();
                if (tuesdayEnd.equals(""))
                    tuesdayEnd = "End Time";

                // Wednesday Init
                String wednesdayStart = weeklyAvailabilities.getWednesdayStart();
                if (wednesdayStart.equals(""))
                    wednesdayStart = "Start Time";

                String wednesdayEnd = weeklyAvailabilities.getWednesdayEnd();
                if (wednesdayEnd.equals(""))
                    wednesdayEnd = "End Time";

                // Thursday Init
                String thursdayStart = weeklyAvailabilities.getThursdayStart();
                if (thursdayStart.equals(""))
                    thursdayStart = "Start Time";

                String thursdayEnd = weeklyAvailabilities.getThursdayEnd();
                if (thursdayEnd.equals(""))
                    thursdayEnd = "End Time";

                // Friday Init
                String fridayStart = weeklyAvailabilities.getFridayStart();
                if (fridayStart.equals(""))
                    fridayStart = "Start Time";

                String fridayEnd = weeklyAvailabilities.getFridayEnd();
                if (fridayEnd.equals(""))
                    fridayEnd = "End Time";

                // Saturday Init
                String saturdayStart = weeklyAvailabilities.getSaturdayStart();
                if (saturdayStart.equals(""))
                    saturdayStart = "Start Time";

                String saturdayEnd = weeklyAvailabilities.getSaturdayEnd();
                if (saturdayEnd.equals(""))
                    saturdayEnd = "End Time";

                // Sunday Init
                String sundayStart = weeklyAvailabilities.getSundayStart();
                if (sundayStart.equals(""))
                    sundayStart = "Start Time";

                String sundayEnd = weeklyAvailabilities.getSundayEnd();
                if (sundayEnd.equals(""))
                    sundayEnd = "End Time";

                view.displayTimes(
                        mondayStart, mondayEnd,
                        tuesdayStart, tuesdayEnd,
                        wednesdayStart, wednesdayEnd,
                        thursdayStart, thursdayEnd,
                        fridayStart, fridayEnd,
                        saturdayStart, saturdayEnd,
                        sundayStart, sundayEnd
                );
                
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
     * @param id the id of the clicked object.
     */
    public void setTime(String time, String id) {
        ServiceProvider provider = (ServiceProvider) CurrentAccount.getInstance().getCurrentAccount();
        WeeklyAvailabilities weeklyAvailabilities = provider.getAvailabilities();

        switch (id.toLowerCase()) {
            case "mondaystart":
                weeklyAvailabilities.setMondayStart(time);
                break;
            case "mondayend":
                weeklyAvailabilities.setMondayEnd(time);
                break;
            case "tuesdaystart":
                weeklyAvailabilities.setTuesdayStart(time);
                break;
            case "tuesdayend":
                weeklyAvailabilities.setTuesdayEnd(time);
                break;
            case "wednesdaystart":
                weeklyAvailabilities.setWednesdayStart(time);
                break;
            case "wednesdayend":
                weeklyAvailabilities.setWednesdayEnd(time);
                break;
            case "thursdaystart":
                weeklyAvailabilities.setThursdayStart(time);
                break;
            case "thursdayend":
                weeklyAvailabilities.setThursdayEnd(time);
                break;
            case "fridaystart":
                weeklyAvailabilities.setFridayStart(time);
                break;
            case "fridayend":
                weeklyAvailabilities.setFridayEnd(time);
                break;
            case "saturdaystart":
                weeklyAvailabilities.setSaturdayStart(time);
                break;
            case "saturdayend":
                weeklyAvailabilities.setSaturdayEnd(time);
                break;
            case "sundaystart":
                weeklyAvailabilities.setSundayStart(time);
                break;
            case "sundayend":
                weeklyAvailabilities.setSundayEnd(time);
                break;
        }


    }

    public void saveTimes() {
        ServiceProvider provider = (ServiceProvider) CurrentAccount.getInstance().getCurrentAccount();
        WeeklyAvailabilities weeklyAvailabilities = provider.getAvailabilities();
        if (weeklyAvailabilities.getMondayStart().equals("") ^ weeklyAvailabilities.getMondayEnd().equals("")) {
            view.displayDayInvalid(DayOfWeek.MONDAY);
        } else if (weeklyAvailabilities.getTuesdayStart().equals("") ^ weeklyAvailabilities.getTuesdayEnd().equals("")) {
            view.displayDayInvalid(DayOfWeek.TUESDAY);
        } else if (weeklyAvailabilities.getWednesdayStart().equals("") ^ weeklyAvailabilities.getWednesdayEnd().equals("")) {
            view.displayDayInvalid(DayOfWeek.WEDNESDAY);
        } else if (weeklyAvailabilities.getThursdayStart().equals("") ^ weeklyAvailabilities.getThursdayEnd().equals("")) {
            view.displayDayInvalid(DayOfWeek.THURSDAY);
        } else if (weeklyAvailabilities.getFridayStart().equals("") ^ weeklyAvailabilities.getFridayEnd().equals("")) {
            view.displayDayInvalid(DayOfWeek.FRIDAY);
        } else if (weeklyAvailabilities.getSaturdayStart().equals("") ^ weeklyAvailabilities.getSaturdayEnd().equals("")) {
            view.displayDayInvalid(DayOfWeek.FRIDAY);
        } else if (weeklyAvailabilities.getSundayStart().equals("") ^ weeklyAvailabilities.getSundayEnd().equals("")) {

        } else {
            repository.setAvailabilities(weeklyAvailabilities);
            view.displaySuccesfulSave();
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
        String compare = id.toLowerCase();
        boolean isStart = !compare.contains("start");

        switch (compare) {
            case "mondaystart":
                 time = weeklyAvailabilities.getMondayEnd();
                if (time.equals(""))
                    time = "23:30";
                break;
            case "mondayend":
                time = weeklyAvailabilities.getMondayStart();
                if (time.equals(""))
                    time = "00:00";
                break;
            case "tuesdaystart":
                time = weeklyAvailabilities.getTuesdayEnd();
                if (time.equals(""))
                    time = "23:30";
                break;
            case "tuesdayend":
                time = weeklyAvailabilities.getTuesdayStart();
                if (time.equals(""))
                    time = "00:00";
                break;
            case "wednesdaystart":
                time = weeklyAvailabilities.getWednesdayEnd();
                if (time.equals(""))
                    time = "23:30";
                break;
            case "wednesdayend":
                time = weeklyAvailabilities.getWednesdayStart();
                if (time.equals(""))
                    time = "00:00";
                break;
            case "thursdaystart":
                time = weeklyAvailabilities.getThursdayEnd();
                if (time.equals(""))
                    time = "23:30";
                break;
            case "thursdayend":
                time = weeklyAvailabilities.getThursdayStart();
                if (time.equals(""))
                    time = "00:00";
                break;
            case "fridaystart":
                time = weeklyAvailabilities.getFridayEnd();
                if (time.equals(""))
                    time = "23:30";
                break;
            case "fridayend":
                time = weeklyAvailabilities.getFridayStart();
                if (time.equals(""))
                    time = "00:00";
                break;
            case "saturdaystart":
                time = weeklyAvailabilities.getSaturdayEnd();
                if (time.equals(""))
                    time = "23:30";
                break;
            case "saturdayend":
                time = weeklyAvailabilities.getSaturdayStart();
                if (time.equals(""))
                    time = "00:00";
                break;
            case "sundaystart":
                time = weeklyAvailabilities.getSundayEnd();
                if (time.equals(""))
                    time = "23:30";
                break;
            case "sundayend":
                time = weeklyAvailabilities.getSundayStart();
                if (time.equals(""))
                    time = "00:00";
                break;
        }

        return new Pair<>(time, isStart);
    }

}
