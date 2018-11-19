package com.uottawa.bigbrainmoves.servio.models;

public class Service {
    private String type;
    private double rating;
    private String serviceProviderUser;
    private boolean isOffered;
    private boolean isDisabled;
    private String usernameOfferedDisabled; // combo of username, offered and disabled
    private String typeDisabled; // combo of type and disabled state

    // NEEDED FOR FIREBASE
    public Service() {

    }

    public Service(String type, double rating, String serviceProviderUser, boolean isOffered, boolean isDisabled) {
        this.type = type;
        this.rating = rating;
        this.serviceProviderUser = serviceProviderUser;
        this.isOffered = isOffered;
        this.isDisabled = isDisabled;
        this.usernameOfferedDisabled = serviceProviderUser + String.valueOf(isOffered) + isDisabled;
        this.typeDisabled = type + isDisabled;
    }

    public String getType() {
        return type;
    }

    public double getRating() {
        return rating;
    }

    public String getServiceProviderUser() {
        return serviceProviderUser;
    }

    public String getUsernameOfferedDisabled() {
        return usernameOfferedDisabled;
    }

    public String getTypeDisabled() {
        return typeDisabled;
    }

    public void setServiceProviderUser(String serviceProviderUser) {
        this.serviceProviderUser = serviceProviderUser;
        usernameOfferedDisabled = serviceProviderUser + String.valueOf(isOffered) + isDisabled;
    }

    public void setType(String type) {
        this.type = type;
        typeDisabled = type + isDisabled;
    }

    public void setRating(double rating) {
        this.rating = rating;
    }

    public boolean isOffered() {
        return isOffered;
    }

    public boolean isDisabled() {
        return isDisabled;
    }

    public void setDisabled(boolean disabled) {
        isDisabled = disabled;
        usernameOfferedDisabled = serviceProviderUser + String.valueOf(isOffered) + isDisabled;
        typeDisabled = type + isDisabled;
    }


    public void setOffered(boolean offered) {
        isOffered = offered;
        usernameOfferedDisabled = serviceProviderUser + String.valueOf(isOffered) + isDisabled;

    }
}
