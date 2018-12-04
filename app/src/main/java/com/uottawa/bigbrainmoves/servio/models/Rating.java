package com.uottawa.bigbrainmoves.servio.models;

import com.google.firebase.database.Exclude;

import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.Locale;
import java.util.Objects;

public class Rating {
    private String serviceTypeProviderRater;
    private String serviceTypeProvider;
    private String raterUser;
    private String comment;
    private double ratingGiven;
    private Date reviewDate;
    private static final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm", Locale.ENGLISH);

    public Rating() {
        // Required for firebase
    }
    public Rating(String raterUser, String comment, String serviceType, String providerUser, double ratingGiven, long reviewDate) {
        this.raterUser = raterUser;
        this.comment = comment;
        this.ratingGiven = ratingGiven;
        this.serviceTypeProvider = serviceType + providerUser;
        this.reviewDate = new Date(reviewDate);
        serviceTypeProviderRater = serviceTypeProvider + raterUser;
    }

    public long getReviewDate() {
        return reviewDate.getTime();
    }

    @Exclude
    public String getStringDate() {
        return dateFormat.format(reviewDate);
    }

    public void setReviewDate(long reviewDate) {
        this.reviewDate = new Date(reviewDate);
    }



    public String getServiceTypeProviderRater() {
        return serviceTypeProviderRater;
    }

    public String getServiceTypeProvider() {
        return serviceTypeProvider;
    }


    public String getRaterUser() {
        return raterUser;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public double getRatingGiven() {
        return ratingGiven;
    }

    public void setRatingGiven(double ratingGiven) {
        this.ratingGiven = ratingGiven;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Rating ratingOther = (Rating) o;
        return Objects.equals(serviceTypeProviderRater, ratingOther.serviceTypeProviderRater);
    }

    @Override
    public int hashCode() {
        return Objects.hash(serviceTypeProviderRater);
    }
}
