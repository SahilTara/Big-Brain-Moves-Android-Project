package com.uottawa.bigbrainmoves.servio.models;

import com.google.firebase.database.Exclude;

import java.util.Objects;

public class Service {
    private String type;
    private String serviceProviderUser;
    private boolean isOffered;
    private boolean isDisabled;
    private String usernameOfferedDisabled; // combo of username, offered and disabled
    private String typeDisabled; // combo of type and disabled state
    private String offeredDisabled; // combo of offered and disabled.
    private int numOfRatings;
    private double totalRatingSum;
    private String serviceProviderName;

    // NEEDED FOR FIREBASE
    public Service() {

    }

    public Service(String type, String serviceProviderUser, String serviceProviderName, int numOfRatings, double totalRatingSum,
                   boolean isOffered, boolean isDisabled) {
        this.type = type;
        this.serviceProviderUser = serviceProviderUser;
        this.isOffered = isOffered;
        this.isDisabled = isDisabled;
        this.usernameOfferedDisabled = serviceProviderUser + String.valueOf(isOffered) + isDisabled;
        this.typeDisabled = type + isDisabled;
        this.numOfRatings = numOfRatings;
        this.totalRatingSum = totalRatingSum;
        this.serviceProviderName = serviceProviderName;
        this.offeredDisabled = String.valueOf(isOffered) + String.valueOf(isDisabled);

    }

    public String getOfferedDisabled() {
        return offeredDisabled;
    }

    public String getType() {
        return type;
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

    public double getTotalRatingSum() {
        return totalRatingSum;
    }

    public int getNumOfRatings() {
        return numOfRatings;
    }

    public String getServiceProviderName() {
        return serviceProviderName;
    }

    public void setServiceProviderName(String serviceProviderName) {
        this.serviceProviderName = serviceProviderName;
    }

    public void setNumOfRatings(int numOfRatings) {
        this.numOfRatings = numOfRatings;
    }

    public void setTotalRatingSum(double totalRatingSum) {
        this.totalRatingSum = totalRatingSum;
    }

    @Exclude
    public double getRating() {
        if (numOfRatings > 0)
            return totalRatingSum / numOfRatings;

        return 0;
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
        offeredDisabled = String.valueOf(isOffered) + String.valueOf(isDisabled);
    }


    public void setOffered(boolean offered) {
        isOffered = offered;
        usernameOfferedDisabled = serviceProviderUser + String.valueOf(isOffered) + isDisabled;
        offeredDisabled = String.valueOf(isOffered) + String.valueOf(isDisabled);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Service) {
            Service tmp = (Service) obj;
            return this.type.equals(tmp.type) && this.serviceProviderUser.equals(tmp.serviceProviderUser)
                    && this.serviceProviderName.equals(tmp.serviceProviderName);
        } else {
            return false;
        }
    }

    @Override
    public int hashCode() {
        return Objects.hash(type, serviceProviderUser, serviceProviderName);
    }
}
