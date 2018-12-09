package com.uottawa.bigbrainmoves.servio.models;

import com.google.firebase.database.Exclude;
import com.uottawa.bigbrainmoves.servio.util.enums.DayOfWeek;

import java.util.EnumMap;
import java.util.Map;
import java.util.Optional;
import java.util.regex.Pattern;

public class WeeklyAvailabilities {
    private Map<DayOfWeek, TimeSlot> slots = new EnumMap<>(DayOfWeek.class);

    private static final Pattern VALID_TIME = Pattern.compile("\\d{2}:\\d{2}");


    public WeeklyAvailabilities() {
        slots.put(DayOfWeek.MONDAY, new TimeSlot());
        slots.put(DayOfWeek.TUESDAY, new TimeSlot());
        slots.put(DayOfWeek.WEDNESDAY, new TimeSlot());
        slots.put(DayOfWeek.THURSDAY, new TimeSlot());
        slots.put(DayOfWeek.FRIDAY, new TimeSlot());
        slots.put(DayOfWeek.SATURDAY, new TimeSlot());
        slots.put(DayOfWeek.SUNDAY, new TimeSlot());
    }

    public TimeSlot getTimeSlotOnDay(DayOfWeek day) {
        if (day.equals(DayOfWeek.ANY))
            throw new IllegalArgumentException("You may not pass DayOfWeek with a value of ANY to getTimeSlotOnDay");
        return slots.get(day);
    }

    public void setTimeSlotOnDay(DayOfWeek day, TimeSlot timeSlot) {
        if (day.equals(DayOfWeek.ANY))
            throw new IllegalArgumentException("You may not pass DayOfWeek with a value of ANY to setTimeSlotOnDay");
        slots.replace(day, timeSlot);
    }



    public boolean checkIfAvailable(DayOfWeek dayOfWeek, String time, boolean filterByTime) {

        if (!dayOfWeek.equals(DayOfWeek.ANY)) {
            TimeSlot slot = slots.get(dayOfWeek);
            if (slot == null)
                return false;

            String start = slot.getStartTime();
            String end = slot.getEndTime();
            return isValidTimeOrNotFiltered(start, end, time, filterByTime);
        }

        for (Map.Entry<DayOfWeek, TimeSlot> timeSlotEntry : slots.entrySet()) {
            TimeSlot slot = timeSlotEntry.getValue();
            String start = slot.getStartTime();
            String end = slot.getEndTime();
            if (isValidTimeOrNotFiltered(start, end, time, filterByTime))
                return true;
        }
        return false;

    }

    private boolean isValidTimeOrNotFiltered(String start, String end, String time, boolean filterByTime) {
        return ((time.compareTo(start) >= 0 // check if >= than start
                && time.compareTo(end) < 0) || !filterByTime) // less than end
                && VALID_TIME.matcher(start).matches();
    }

    @Exclude
    public Optional<DayOfWeek> getInvalidTimeSlot() {
        DayOfWeek invalidDay = null;

        for (Map.Entry<DayOfWeek, TimeSlot> timeSlotEntry : slots.entrySet()) {
            if (!timeSlotEntry.getValue().isValid()) {
                invalidDay = timeSlotEntry.getKey();
                break;
            }
        }
        
        return Optional.ofNullable(invalidDay);
    }
}
