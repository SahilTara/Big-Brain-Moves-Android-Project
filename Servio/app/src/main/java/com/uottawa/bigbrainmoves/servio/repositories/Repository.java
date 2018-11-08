package com.uottawa.bigbrainmoves.servio.repositories;

import com.uottawa.bigbrainmoves.servio.models.Account;
import com.uottawa.bigbrainmoves.servio.util.SignupResult;

import java.util.List;

import io.reactivex.Observable;

public interface Repository {
    Observable<Account> getUserFromDataBase(String uid);
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
}
