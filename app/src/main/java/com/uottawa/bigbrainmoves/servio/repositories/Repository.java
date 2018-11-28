package com.uottawa.bigbrainmoves.servio.repositories;

import com.uottawa.bigbrainmoves.servio.models.Account;
import com.uottawa.bigbrainmoves.servio.models.Service;
import com.uottawa.bigbrainmoves.servio.models.ServiceType;
import com.uottawa.bigbrainmoves.servio.models.WeeklyAvailabilities;
import com.uottawa.bigbrainmoves.servio.util.Pair;
import com.uottawa.bigbrainmoves.servio.util.SignupResult;

import java.util.HashMap;
import java.util.List;
import java.util.Optional;

import io.reactivex.Observable;

public interface Repository {
    Observable<Optional<Account>> getUserFromDataBase(String uid);
    Observable<List<Account>> getAllUsersFromDataBase();
    Observable<Boolean> login(String input, String password);
    Observable<SignupResult> createUserIfNotInDataBase(final String email,
                                                       final String username,
                                                       final String password,
                                                       final String displayName,
                                                       final String typeSelected);
    Observable<Boolean> doesAdminAccountExist();
    boolean checkIfUserIsLoggedIn();
    String getUserId();
    void signOutCurrentUser();

    Observable<Boolean> createServiceTypeIfNotInDatabase(String serviceTypeName, double value);
    Observable<Pair<ServiceType, Boolean>> listenForServiceTypeChanges();
    void deleteServiceType(String serviceTypeName);
    void editServiceType(String serviceTypeName, double value);

    Observable<List<Service>> getServicesProvidedByCurrentUser();

    Observable<List<Service>> getServicesProvidable(List<String> provided);

    Observable<Pair<Service, Boolean>> listenForServiceChanges();

    Observable<Boolean> saveProfile(String phoneNumber,
                                    String address, String companyName,
                                    String description, boolean isLicensed,
                                    List<Service> modified);

    Observable<Optional<WeeklyAvailabilities>> getAvailabilities();
    Observable<HashMap<String, WeeklyAvailabilities>> getAllAvailabilities();
    void setAvailabilities(WeeklyAvailabilities availabilities);
}
