package com.uottawa.bigbrainmoves.servio;

public class User {
    // NEED TO BE PUBLIC FOR FIREBASE
    public String displayName;
    public String type;
    public String username;

    public User() {
        // Needed for DB
    }

    public User(String displayName, String type, String username) {
        this.displayName = displayName;
        this.type = type;
        this.username = username;
    }

    public String getDisplayName() { return displayName; }

    public String getType() { return type; }

    public String getUsername() { return username; }

}
