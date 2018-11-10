package com.uottawa.bigbrainmoves.servio.views;

public interface CreateServiceTypeView {
    void displayInvalidName();
    void displayInvalidValue();
    void displayNameTaken();
    void displaySuccess();
    void displayDataError();
}
