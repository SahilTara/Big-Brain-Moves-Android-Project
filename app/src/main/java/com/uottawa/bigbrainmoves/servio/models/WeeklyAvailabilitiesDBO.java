package com.uottawa.bigbrainmoves.servio.models;

import com.uottawa.bigbrainmoves.servio.util.enums.DayOfWeek;

import java.util.HashMap;
import java.util.Map;

public class WeeklyAvailabilitiesDBO {
    private Map<String, TimeSlot> slots = new HashMap<>();

    public WeeklyAvailabilitiesDBO() {
        this(new WeeklyAvailabilities());
    }

    public WeeklyAvailabilitiesDBO(WeeklyAvailabilities weeklyAvailabilities) {
        for (DayOfWeek dayOfWeek : DayOfWeek.values()) {
            if (dayOfWeek.equals(DayOfWeek.ANY))
                break;

            slots.put(dayOfWeek.name(), weeklyAvailabilities.getTimeSlotOnDay(dayOfWeek));
        }
    }

    public Map<String, TimeSlot> getSlots() {
        return slots;
    }

    public void setSlots(Map<String, TimeSlot> slots) {
        this.slots = slots;
    }

    public WeeklyAvailabilities convertToDomainObject() {
        WeeklyAvailabilities weeklyAvailabilities = new WeeklyAvailabilities();

        for (Map.Entry<String, TimeSlot> slotEntry : slots.entrySet()) {
            DayOfWeek dayOfWeek  = DayOfWeek.valueOf(slotEntry.getKey());
            weeklyAvailabilities.setTimeSlotOnDay(dayOfWeek, slotEntry.getValue());
        }

        return  weeklyAvailabilities;
    }

}
