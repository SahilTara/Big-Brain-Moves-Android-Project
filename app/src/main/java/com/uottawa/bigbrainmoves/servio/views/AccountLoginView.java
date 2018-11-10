package com.uottawa.bigbrainmoves.servio.views;

public interface AccountLoginView {
    /**
     * Method to be called when no account can be found
     */
    void displayNoAccountFound();

    /**
     * Method to be called when the database gets an error.
     */
    void displayDataError();

    /**
     * Method to be called when the account is found and valid.
     */
    void displayValidAccount();
}
