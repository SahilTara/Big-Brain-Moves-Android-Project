package com.uottawa.bigbrainmoves.servio;

public interface DbHandler {

    void login(String input, String password);
    void getSignedInUser();
    void createUser(final String email, final String username, final String password,
                       final String displayName, final String type);

}
