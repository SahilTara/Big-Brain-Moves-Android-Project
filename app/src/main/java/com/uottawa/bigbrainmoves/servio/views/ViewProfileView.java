package com.uottawa.bigbrainmoves.servio.views;

import com.uottawa.bigbrainmoves.servio.models.Service;

import java.util.List;

public interface ViewProfileView {
    void displayServiceProviderInfo(String phoneNumber, String address, String companyName,
                                    String description, boolean isLicensed,
                                    List<Service> offered, List<Service> offerable);

    void displayInvalidPhoneNumber();
    void displayInvalidAddress();
    void displayInvalidCompanyName();
    void displaySuccessfullySaved();
    void displaySaveUnsuccessful();
    void displayDbError();
}
