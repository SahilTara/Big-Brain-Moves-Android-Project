package com.uottawa.bigbrainmoves.servio.views;

import com.uottawa.bigbrainmoves.servio.models.WeeklyAvailabilities;
import com.uottawa.bigbrainmoves.servio.util.enums.DayOfWeek;

public interface ManageAvailabilitiesView {
    void displayDbError();
    void displaySuccessfulSave();
    void displayTimes(WeeklyAvailabilities weeklyAvailabilities);

    void displayDayInvalid(DayOfWeek day);
}
