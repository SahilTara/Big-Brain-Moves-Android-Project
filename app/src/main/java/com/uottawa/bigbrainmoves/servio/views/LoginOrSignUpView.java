package com.uottawa.bigbrainmoves.servio.views;

public interface LoginOrSignUpView extends AccountLoginView {
    void displayValidLogin();
    void displayInvalidLogin();
    void displayInvalidUser();
    void displayInvalidPassword();
}
