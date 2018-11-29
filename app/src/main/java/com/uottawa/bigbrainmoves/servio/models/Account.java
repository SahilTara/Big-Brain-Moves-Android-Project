package com.uottawa.bigbrainmoves.servio.models;


import com.uottawa.bigbrainmoves.servio.util.enums.AccountType;

public class Account {
    private String displayName;
    private AccountType type;
    private String username;

    public Account() {
        // Needed for DB
    }

    public Account(String displayName, AccountType type, String username) {
        this.displayName = displayName;
        this.type = type;
        this.username = username;
    }

    public String getDisplayName() { return displayName; }

    public AccountType getType() { return type; }

    public String getUsername() { return username; }

}
