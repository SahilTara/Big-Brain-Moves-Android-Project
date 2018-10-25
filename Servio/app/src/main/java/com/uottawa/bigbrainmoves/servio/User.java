package com.uottawa.bigbrainmoves.servio;

public class User {
    // NEED TO BE PUBLIC FOR FIREBASE
    public String displayName;
    public String type;

    public User() {
        // Needed for DB
    }

    public User(String displayName, String type) {
        this.displayName = displayName;
        this.type = type;
    }

    public String getDisplayName() { return displayName; }

    public String getType() { return type; }

}
