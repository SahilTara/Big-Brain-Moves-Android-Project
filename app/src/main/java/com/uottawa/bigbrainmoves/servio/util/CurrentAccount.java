package com.uottawa.bigbrainmoves.servio.util;

import com.uottawa.bigbrainmoves.servio.models.Account;

public class CurrentAccount {
    private static CurrentAccount instance;
    private Account currentAccount;


    private CurrentAccount() {}

    /**
     * Classic Singleton Get Instance.
     * @return
     */
    public static CurrentAccount getInstance() {
        if (instance == null) instance = new CurrentAccount();
        return instance;
    }

    /**
     * Mutator for current account,
     * @param account the account to set the current account to.
     */
    public void setCurrentAccount(Account account) {
        currentAccount = account;
    }

    /**
     * Accessor for the current user.
     * @return the currently logged in user.
     */
    public Account getCurrentAccount() {
        return currentAccount;
    }
}
