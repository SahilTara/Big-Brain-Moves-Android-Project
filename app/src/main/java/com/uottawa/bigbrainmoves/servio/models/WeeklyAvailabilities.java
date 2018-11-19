package com.uottawa.bigbrainmoves.servio.models;

public class WeeklyAvailabilities {
    private String mondayStart;
    private String mondayEnd;
    private String tuesdayStart;
    private String tuesdayEnd;
    private String wednesdayStart;
    private String wednesdayEnd;
    private String thursdayStart;
    private String thursdayEnd;
    private String fridayStart;
    private String fridayEnd;
    private String saturdayStart;
    private String saturdayEnd;
    private String sundayStart;
    private String sundayEnd;

    // Needed for firebase
    public WeeklyAvailabilities() {
        mondayStart = "";
        mondayEnd = "";

        tuesdayStart = "";
        tuesdayEnd = "";

        wednesdayStart = "";
        wednesdayEnd = "";

        thursdayStart = "";
        thursdayEnd = "";

        fridayStart = "";
        fridayEnd = "";

        saturdayStart = "";
        saturdayEnd = "";

        sundayStart = "";
        sundayEnd = "";
    }

    public WeeklyAvailabilities(String mondayStart, String mondayEnd,
                                String tuesdayStart, String tuesdayEnd,
                                String wednesdayStart, String wednesdayEnd,
                                String thursdayStart, String thursdayEnd,
                                String fridayStart, String fridayEnd,
                                String saturdayStart, String saturdayEnd,
                                String sundayStart, String sundayEnd) {
        this.mondayStart = mondayStart;
        this.mondayEnd = mondayEnd;
        this.tuesdayStart = tuesdayStart;
        this.tuesdayEnd = tuesdayEnd;
        this.wednesdayStart = wednesdayStart;
        this.wednesdayEnd = wednesdayEnd;
        this.thursdayStart = thursdayStart;
        this.thursdayEnd = thursdayEnd;
        this.fridayStart = fridayStart;
        this.fridayEnd = fridayEnd;
        this.saturdayStart = saturdayStart;
        this.saturdayEnd = saturdayEnd;
        this.sundayStart = sundayStart;
        this.sundayEnd = sundayEnd;
    }

    public String getMondayEnd() {
        return mondayEnd;
    }

    public String getMondayStart() {
        return mondayStart;
    }

    public String getTuesdayEnd() {
        return tuesdayEnd;
    }

    public String getTuesdayStart() {
        return tuesdayStart;
    }

    public String getWednesdayEnd() {
        return wednesdayEnd;
    }

    public String getWednesdayStart() {
        return wednesdayStart;
    }

    public String getThursdayEnd() {
        return thursdayEnd;
    }

    public String getThursdayStart() {
        return thursdayStart;
    }

    public String getFridayStart() {
        return fridayStart;
    }

    public String getFridayEnd() {
        return fridayEnd;
    }

    public String getSaturdayEnd() {
        return saturdayEnd;
    }

    public String getSaturdayStart() {
        return saturdayStart;
    }

    public String getSundayEnd() {
        return sundayEnd;
    }

    public String getSundayStart() {
        return sundayStart;
    }

    public void setMondayStart(String mondayStart) {
        this.mondayStart = mondayStart;
    }

    public void setMondayEnd(String mondayEnd) {
        this.mondayEnd = mondayEnd;
    }

    public void setTuesdayStart(String tuesdayStart) {
        this.tuesdayStart = tuesdayStart;
    }

    public void setTuesdayEnd(String tuesdayEnd) {
        this.tuesdayEnd = tuesdayEnd;
    }

    public void setWednesdayStart(String wednesdayStart) {
        this.wednesdayStart = wednesdayStart;
    }

    public void setWednesdayEnd(String wednesdayEnd) {
        this.wednesdayEnd = wednesdayEnd;
    }

    public void setThursdayStart(String thursdayStart) {
        this.thursdayStart = thursdayStart;
    }

    public void setThursdayEnd(String thursdayEnd) {
        this.thursdayEnd = thursdayEnd;
    }

    public void setFridayStart(String fridayStart) {
        this.fridayStart = fridayStart;
    }

    public void setFridayEnd(String fridayEnd) {
        this.fridayEnd = fridayEnd;
    }

    public void setSaturdayStart(String saturdayStart) {
        this.saturdayStart = saturdayStart;
    }

    public void setSaturdayEnd(String saturdayEnd) {
        this.saturdayEnd = saturdayEnd;
    }

    public void setSundayStart(String sundayStart) {
        this.sundayStart = sundayStart;
    }

    public void setSundayEnd(String sundayEnd) {
        this.sundayEnd = sundayEnd;
    }
}
