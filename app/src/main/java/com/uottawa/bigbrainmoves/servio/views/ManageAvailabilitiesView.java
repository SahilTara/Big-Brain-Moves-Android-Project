package com.uottawa.bigbrainmoves.servio.views;

import com.uottawa.bigbrainmoves.servio.util.DayOfWeek;

public interface ManageAvailabilitiesView {
    void displayDbError();
    void displaySuccesfulSave();
    void displayTimes(String mondayStart, String mondayEnd,
                      String tuesdayStart, String tuesdayEnd,
                      String wednesdayStart, String wednesdayEnd,
                      String thursdayStart, String thursdayEnd,
                      String fridayStart, String fridayEnd,
                      String saturdayStart, String saturdayEnd,
                      String sundayStart, String sundayEnd);

    void displayDayInvalid(DayOfWeek day);
}
