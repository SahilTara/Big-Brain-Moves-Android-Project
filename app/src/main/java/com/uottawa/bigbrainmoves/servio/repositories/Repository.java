package com.uottawa.bigbrainmoves.servio.repositories;

import com.uottawa.bigbrainmoves.servio.models.Account;
import com.uottawa.bigbrainmoves.servio.models.Rating;
import com.uottawa.bigbrainmoves.servio.models.ReadOnlyService;
import com.uottawa.bigbrainmoves.servio.models.Service;
import com.uottawa.bigbrainmoves.servio.models.ServiceProvider;
import com.uottawa.bigbrainmoves.servio.models.ServiceType;
import com.uottawa.bigbrainmoves.servio.util.Pair;
import com.uottawa.bigbrainmoves.servio.util.enums.AccountType;
import com.uottawa.bigbrainmoves.servio.util.enums.SignupResult;

import java.util.List;
import java.util.Optional;

import io.reactivex.Observable;

public interface Repository extends AvailabilitiesRepository, BookingsRepository {
    Observable<Optional<ServiceProvider>> getServiceProviderFromDatabase(String username);
    Observable<Optional<Account>> getUserFromDataBase(String uid);
    Observable<List<Account>> getAllUsersFromDataBase();
    Observable<Boolean> login(String input, String password);
    Observable<SignupResult> createUserIfNotInDataBase(final String email,
                                                       final String username,
                                                       final String password,
                                                       final String displayName,
                                                       final AccountType typeSelected);
    Observable<Boolean> doesAdminAccountExist();
    boolean checkIfUserIsLoggedIn();
    String getUserId();
    void signOutCurrentUser();

    Observable<Boolean> createServiceTypeIfNotInDatabase(String serviceTypeName, double value);
    Observable<Pair<ServiceType, Boolean>> listenForServiceTypeChanges();
    Observable<Optional<ServiceType>> getServiceType(String serviceTypeName);
    void deleteServiceType(String serviceTypeName);
    void editServiceType(String serviceTypeName, double value);

    Observable<List<Service>> getServicesProvidedByCurrentUser();

    Observable<List<Service>> getServicesProvidable(List<String> provided);

    Observable<Optional<Service>> getServiceByName(String serviceTypeProvider);

    Observable<Pair<Service, Boolean>> listenForServiceChanges();

    Observable<Boolean> saveProfile(String phoneNumber,
                                    String address, String companyName,
                                    String description, boolean isLicensed,
                                    List<Service> modified);

    Observable<Boolean> addRating(Rating rating, double oldRating, boolean isUpdate);
    Observable<Optional<Rating>> getRating(String providerUser, String serviceType, String rater);
    Observable<Pair<Rating, Boolean>> listenForRatingChanges(ReadOnlyService service);
}
