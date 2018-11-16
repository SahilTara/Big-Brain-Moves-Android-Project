package com.uottawa.bigbrainmoves.servio.views;

public interface SignUpView extends AccountLoginView {
    void displayEmailTaken();
    void displayUserNameTaken();
    void displaySignUpSuccess();

    void displayInvalidEmail();
    void displayInvalidUserNameLength();
    void displayInvalidUserNameAlphanumeric();
    void displayInvalidDisplayName();
    void displayInvalidPassword();
    void displayInvalidType();

    void displayAdminDoesNotExist();
}
