package com.uottawa.bigbrainmoves.servio.models;


public class Account {
    private String displayName;
    private String type;
    private String username;

    public Account() {
        // Needed for DB
    }

    public Account(String displayName, String type, String username) {
        this.displayName = displayName;
        this.type = type;
        this.username = username;
    }

    public String getDisplayName() { return displayName; }

    public String getType() { return type; }

    public String getUsername() { return username; }

}
