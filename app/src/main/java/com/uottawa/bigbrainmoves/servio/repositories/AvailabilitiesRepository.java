package com.uottawa.bigbrainmoves.servio.repositories;

import com.uottawa.bigbrainmoves.servio.models.WeeklyAvailabilities;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import io.reactivex.Observable;

public interface AvailabilitiesRepository {
    void setAvailabilities(WeeklyAvailabilities availabilities);
    Observable<Optional<WeeklyAvailabilities>> getAvailabilities();
    Observable<Optional<WeeklyAvailabilities>> getAvailabilities(String username);
    Observable<Map<String, WeeklyAvailabilities>> getAllAvailabilities();
}
