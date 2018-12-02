package com.uottawa.bigbrainmoves.servio.models;

public interface ReadOnlyService {
    String getType();
    String getServiceProviderName();
    String getServiceProviderUser();
    double getRating();
}
