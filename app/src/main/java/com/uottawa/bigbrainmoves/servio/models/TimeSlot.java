package com.uottawa.bigbrainmoves.servio.models;

import com.google.firebase.database.Exclude;

public class TimeSlot {
    private String startTime;
    private String endTime;

    private static final String DEFAULT_START_STRING = "Start Time";
    private static final String DEFAULT_END_STRING = "End Time";

    public TimeSlot() {
        startTime = DEFAULT_START_STRING;
        endTime = DEFAULT_END_STRING;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    public String getStartTime() {
        return startTime;
    }

    public String getEndTime() {
        return endTime;
    }

    @Exclude
    public boolean isValid() {
        return startTime.equals(DEFAULT_START_STRING) == endTime.equals(DEFAULT_END_STRING);
    }
}
