package com.uottawa.bigbrainmoves.servio.views;

import com.uottawa.bigbrainmoves.servio.models.Account;

import java.util.List;

public interface UserListView {
    /**
     * Method to be called when the database gets an error.
     */
    void displayDataError();

    /**
     * Method called to display the passed accounts list.
     */
    void displayUsers(List<Account> accounts);
}
