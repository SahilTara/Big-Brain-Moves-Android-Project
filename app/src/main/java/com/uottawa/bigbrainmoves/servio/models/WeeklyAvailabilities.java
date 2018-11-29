package com.uottawa.bigbrainmoves.servio.models;

import com.google.firebase.database.Exclude;
import com.uottawa.bigbrainmoves.servio.util.enums.DayOfWeek;

import java.util.Optional;

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

    public WeeklyAvailabilities() {
        mondayStart = "Start Time";
        mondayEnd = "End Time";

        tuesdayStart = "Start Time";
        tuesdayEnd = "End Time";

        wednesdayStart = "Start Time";
        wednesdayEnd = "End Time";

        thursdayStart = "Start Time";
        thursdayEnd = "End Time";

        fridayStart = "Start Time";
        fridayEnd = "End Time";

        saturdayStart = "Start Time";
        saturdayEnd = "End Time";

        sundayStart = "Start Time";
        sundayEnd = "End Time";
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

    @Exclude
    public Optional<DayOfWeek> getInvalidTimeSlot() {
        DayOfWeek invalidDay = null;
        
        if (mondayStart.equals("Start Time") ^ mondayEnd.equals("End Time")) {
            invalidDay = DayOfWeek.MONDAY;
        } else if (tuesdayStart.equals("Start Time") ^ tuesdayEnd.equals("End Time")) {
            invalidDay = DayOfWeek.TUESDAY;
        } else if (wednesdayStart.equals("Start Time") ^ wednesdayEnd.equals("End Time")) {
            invalidDay = DayOfWeek.WEDNESDAY;
        } else if (thursdayStart.equals("Start Time") ^ thursdayEnd.equals("End Time")) {
            invalidDay = DayOfWeek.THURSDAY;
        } else if (fridayStart.equals("Start Time") ^ fridayEnd.equals("End Time")) {
            invalidDay = DayOfWeek.FRIDAY;
        } else if (saturdayStart.equals("Start Time") ^ saturdayEnd.equals("End Time")) {
            invalidDay = DayOfWeek.SATURDAY;
        } else if (sundayStart.equals("Start Time") ^ sundayEnd.equals("End Time")) {
            invalidDay = DayOfWeek.SUNDAY;
        }
        
        return Optional.ofNullable(invalidDay);
    }

    public enum TimeSlot {
        MONDAY_START {
            @Override
            public String getMethodName() {
                return "setMondayStart";
            }
        },
        MONDAY_END {
            @Override
            public String getMethodName() {
                return "setMondayEnd";
            }
        },
        TUESDAY_START {
            @Override
            public String getMethodName() {
                return "setTuesdayStart";
            }
        },
        TUESDAY_END {
            @Override
            public String getMethodName() {
                return "setTuesdayEnd";
            }
        },
        WEDNESDAY_START {
            @Override
            public String getMethodName() {
                return "setWednesdayStart";
            }
        },
        WEDNESDAY_END {
            @Override
            public String getMethodName() {
                return "setWednesdayEnd";
            }
        },
        THURSDAY_START {
            @Override
            public String getMethodName() {
                return "setThursdayStart";
            }
        },
        THURSDAY_END {
            @Override
            public String getMethodName() {
                return "setThursdayEnd";
            }
        },
        FRIDAY_START {
            @Override
            public String getMethodName() {
                return "setFridayStart";
            }
        },
        FRIDAY_END {
            @Override
            public String getMethodName() {
                return "setFridayEnd";
            }
        },
        SATURDAY_START {
            @Override
            public String getMethodName() {
                return "setSaturdayStart";
            }
        },
        SATURDAY_END {
            @Override
            public String getMethodName() {
                return "setSaturdayEnd";
            }
        },
        SUNDAY_START {
            @Override
            public String getMethodName() {
                return "setSundayStart";
            }
        },
        SUNDAY_END {
            @Override
            public String getMethodName() {
                return "setSundayEnd";
            }
        };

        public abstract String getMethodName();
    }
}
