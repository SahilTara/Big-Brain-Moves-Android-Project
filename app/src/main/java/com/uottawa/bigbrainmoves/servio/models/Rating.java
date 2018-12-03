package com.uottawa.bigbrainmoves.servio.models;

import com.google.firebase.database.Exclude;

import java.sql.Date;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Locale;

public class Rating {
    private String serviceTypeProviderRater;
    private String serviceTypeProvider;
    private String raterUser;
    private String comment;
    private double rating;
    private Date reviewDate;
    private static final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm", Locale.ENGLISH);

    public Rating() {
        // Required for firebase
    }
    public Rating(String raterUser, String comment, String serviceType, String providerUser, double rating, long reviewDate) {
        this.raterUser = raterUser;
        this.comment = comment;
        this.rating = rating;
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

    public double getRating() {
        return rating;
    }

    public void setRating(double rating) {
        this.rating = rating;
    }
}
