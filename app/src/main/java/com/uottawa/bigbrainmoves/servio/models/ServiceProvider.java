package com.uottawa.bigbrainmoves.servio.models;

import com.google.firebase.database.Exclude;
import com.uottawa.bigbrainmoves.servio.util.enums.AccountType;

import java.util.ArrayList;
import java.util.List;

public class ServiceProvider extends Account {
    private String phoneNumber;
    private String address;
    private String companyName;
    private String description;
    private boolean isLicensed;

    @Exclude
    private List<Service> servicesProvided;

    @Exclude
    private WeeklyAvailabilities availabilities;

    // NEEDED FOR FIREBASE
    public ServiceProvider() {

    }

    public ServiceProvider(String displayName, String username,
                           String phoneNumber, String address, String companyName,
                           String description, boolean isLicensed) {
        super(displayName, AccountType.SERVICE_PROVIDER, username);
        this.phoneNumber = phoneNumber;
        this.address = address;
        this.companyName = companyName;
        this.description = description;
        this.isLicensed = isLicensed;
    }

    public String getAddress() {
        return address;
    }

    public String getCompanyName() {
        return companyName;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public String getDescription() {
        return description;
    }

    public boolean isLicensed() {
        return isLicensed;
    }

    @Exclude
    public List<Service> getServicesProvided() {
        return new ArrayList<>(servicesProvided);
    }

    @Exclude
    public WeeklyAvailabilities getAvailabilities() {
        return availabilities;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setLicensed(boolean licensed) {
        isLicensed = licensed;
    }

    @Exclude
    public void setServicesProvided(List<Service> servicesProvided) {
        this.servicesProvided = servicesProvided;
    }

    @Exclude
    public void setAvailabilities(WeeklyAvailabilities availabilities) {
        this.availabilities = availabilities;
    }
}
