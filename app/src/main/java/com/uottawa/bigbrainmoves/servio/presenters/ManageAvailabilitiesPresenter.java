package com.uottawa.bigbrainmoves.servio.presenters;

import com.uottawa.bigbrainmoves.servio.models.ServiceProvider;
import com.uottawa.bigbrainmoves.servio.models.TimeSlot;
import com.uottawa.bigbrainmoves.servio.models.WeeklyAvailabilities;
import com.uottawa.bigbrainmoves.servio.repositories.Repository;
import com.uottawa.bigbrainmoves.servio.util.CurrentAccount;
import com.uottawa.bigbrainmoves.servio.util.Pair;
import com.uottawa.bigbrainmoves.servio.util.enums.DayOfWeek;
import com.uottawa.bigbrainmoves.servio.util.enums.TimeSlotEntryType;
import com.uottawa.bigbrainmoves.servio.views.ManageAvailabilitiesView;

import java.util.Optional;

import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class ManageAvailabilitiesPresenter {


    private final ManageAvailabilitiesView view;
    private final Repository repository;
    private static final String DEFAULT_START_STRING = "Start Time";
    private static final String DEFAULT_END_STRING = "End Time";

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
        repository.getAvailabilities()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(observer);
    }


    /**
     * Given an id we extract the time we must set in the weekly availabilities object.
     * @param time the time to set to.
     * @param day the day of the TimeSlot
     * @param type the type of the time slot entry (Start/End)
     */
    public void setTime(String time, DayOfWeek day, TimeSlotEntryType type) {
        ServiceProvider provider = (ServiceProvider) CurrentAccount.getInstance().getCurrentAccount();
        WeeklyAvailabilities weeklyAvailabilities = provider.getAvailabilities();
        TimeSlot timeSlot = weeklyAvailabilities.getTimeSlotOnDay(day);

        if (type == TimeSlotEntryType.START) {
            timeSlot.setStartTime(time);
        } else if (type == TimeSlotEntryType.END) {
            timeSlot.setEndTime(time);
        }

        weeklyAvailabilities.setTimeSlotOnDay(day, timeSlot);
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
     * @param dayOfWeek the day of the TimeSlot
     * @param type the type of the time slot entry (Start/End)
     * @return a Pair containing the time as a string and whether it or not it is a start restriction.
     */
    public Pair<String, Boolean> getTimeRestriction(DayOfWeek dayOfWeek, TimeSlotEntryType type) {
        ServiceProvider provider = (ServiceProvider) CurrentAccount.getInstance().getCurrentAccount();
        WeeklyAvailabilities weeklyAvailabilities = provider.getAvailabilities();
        String time = "";
        boolean isStart = type == TimeSlotEntryType.END;

        TimeSlot slot = weeklyAvailabilities.getTimeSlotOnDay(dayOfWeek);

        if (type == TimeSlotEntryType.START) {
            time = slot.getEndTime();
            if (time.equals(DEFAULT_END_STRING)) {
                time = "23:30";
            }
        } else if (type == TimeSlotEntryType.END) {
            time = slot.getStartTime();
            if (time.equals(DEFAULT_START_STRING)) {
                time = "00:00";
            }
        }

        return new Pair<>(time, isStart);
    }

}
