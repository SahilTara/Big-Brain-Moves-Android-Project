package com.uottawa.bigbrainmoves.servio.models;


import java.util.Objects;

public class ServiceType {
    // NEED TO BE PUBLIC FOR FIREBASE
    private String type; // type of service plumber, washer, etc...
    private double rate;

    public ServiceType() {
        // Needed for DB
    }

    public ServiceType(String type, double rate) {
        this.type = type;
        this.rate = rate;
    }

    public String getType() { return type; }

    public double getRate() { return rate; }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof ServiceType) {
            ServiceType tmp = (ServiceType)obj;
            return this.type.equals(tmp.type);
        } else {
            return false;
        }
    }

    @Override
    public int hashCode() {
        return Objects.hash(type);
    }
}
