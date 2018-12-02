package com.uottawa.bigbrainmoves.servio.models;

public interface ReadOnlyServiceProvider {
    String getAddress();

    String getCompanyName();

    String getPhoneNumber();

    String getDescription();

    //String getUsername();

    boolean isLicensed();
}
