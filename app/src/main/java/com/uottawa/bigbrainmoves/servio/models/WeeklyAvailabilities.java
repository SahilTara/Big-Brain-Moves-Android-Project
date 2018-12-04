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
    private static final String DEFAULT_START_STRING = "Start Time";
    private static final String DEFAULT_END_STRING = "End Time";
    
    public WeeklyAvailabilities() {
        mondayStart = DEFAULT_START_STRING;
        mondayEnd = DEFAULT_END_STRING;

        tuesdayStart = DEFAULT_START_STRING;
        tuesdayEnd = DEFAULT_END_STRING;

        wednesdayStart = DEFAULT_START_STRING;
        wednesdayEnd = DEFAULT_END_STRING;

        thursdayStart = DEFAULT_START_STRING;
        thursdayEnd = DEFAULT_END_STRING;

        fridayStart = DEFAULT_START_STRING;
        fridayEnd = DEFAULT_END_STRING;

        saturdayStart = DEFAULT_START_STRING;
        saturdayEnd = DEFAULT_END_STRING;

        sundayStart = DEFAULT_START_STRING;
        sundayEnd = DEFAULT_END_STRING;
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
        
        if (mondayStart.equals(DEFAULT_START_STRING) ^ mondayEnd.equals(DEFAULT_END_STRING)) {
            invalidDay = DayOfWeek.MONDAY;
        } else if (tuesdayStart.equals(DEFAULT_START_STRING) ^ tuesdayEnd.equals(DEFAULT_END_STRING)) {
            invalidDay = DayOfWeek.TUESDAY;
        } else if (wednesdayStart.equals(DEFAULT_START_STRING) ^ wednesdayEnd.equals(DEFAULT_END_STRING)) {
            invalidDay = DayOfWeek.WEDNESDAY;
        } else if (thursdayStart.equals(DEFAULT_START_STRING) ^ thursdayEnd.equals(DEFAULT_END_STRING)) {
            invalidDay = DayOfWeek.THURSDAY;
        } else if (fridayStart.equals(DEFAULT_START_STRING) ^ fridayEnd.equals(DEFAULT_END_STRING)) {
            invalidDay = DayOfWeek.FRIDAY;
        } else if (saturdayStart.equals(DEFAULT_START_STRING) ^ saturdayEnd.equals(DEFAULT_END_STRING)) {
            invalidDay = DayOfWeek.SATURDAY;
        } else if (sundayStart.equals(DEFAULT_START_STRING) ^ sundayEnd.equals(DEFAULT_END_STRING)) {
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
