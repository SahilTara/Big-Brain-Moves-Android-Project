package com.uottawa.bigbrainmoves.servio;

public class CurrentUser {
    private static User currentUser;

    /**
     * Mutator for current user,
     * @param user the user to set the current user to.
     */
    public void setCurrentUser(User user) {
        currentUser = user;
    }

    /**
     * Accessor for the current user.
     * @return the currently logged in user.
     */
    public User getCurrentUser() {
        return currentUser;
    }
}
