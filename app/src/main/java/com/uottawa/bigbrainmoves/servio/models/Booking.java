package com.uottawa.bigbrainmoves.servio.models;

import com.uottawa.bigbrainmoves.servio.util.enums.BookingStatus;
import com.uottawa.bigbrainmoves.servio.util.enums.DayOfWeek;

import java.util.Objects;

public class Booking {
    private String providerUser;
    private String customerUser;
    private String serviceType;
    private String date;
    private String startTime;
    private String endTime;
    private String providerServiceTypeParsableYearMonthDay;
    private String providerServiceTypeParsableYearMonthDayCustomer;
    private BookingStatus status;
    private double price;

    public Booking() {}

    public Booking(String providerUser, String customerUser, String serviceType,
                   String date, String startTime, String endTime, double price, BookingStatus status) {

        this.providerUser = providerUser;
        this.customerUser = customerUser;
        this.serviceType = serviceType;
        this.startTime = startTime;
        this.endTime = endTime;
        this.price = price;
        this.status = status;
        this.date = date;

        updateProviderServiceTypeParsableYearMonthDay();
        updateProviderServiceTypeParsableYearMonthDayCustomer();
    }

    public String getProviderServiceTypeParsableYearMonthDayCustomer() {
        return providerServiceTypeParsableYearMonthDayCustomer;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;

        updateProviderServiceTypeParsableYearMonthDay();
        updateProviderServiceTypeParsableYearMonthDayCustomer();
    }


    public String getProviderServiceTypeParsableYearMonthDay() {
        return providerServiceTypeParsableYearMonthDay;
    }

    public BookingStatus getStatus() {
        return status;
    }

    public String getServiceType() {
        return serviceType;
    }

    public void setServiceType(String serviceType) {
        this.serviceType = serviceType;

        updateProviderServiceTypeParsableYearMonthDay();
        updateProviderServiceTypeParsableYearMonthDayCustomer();
    }


    private void updateProviderServiceTypeParsableYearMonthDay() {
        if (providerUser == null || serviceType == null || status == null || date == null)
            return;
        providerServiceTypeParsableYearMonthDay = providerUser + serviceType +
                status.isParsable() + date;
    }


    private void updateProviderServiceTypeParsableYearMonthDayCustomer() {
        if (customerUser == null || providerServiceTypeParsableYearMonthDay == null)
            return;

        providerServiceTypeParsableYearMonthDayCustomer = providerServiceTypeParsableYearMonthDay + customerUser;
    }
    public void setStatus(BookingStatus status) {
        this.status = status;

        updateProviderServiceTypeParsableYearMonthDay();
        updateProviderServiceTypeParsableYearMonthDayCustomer();

    }

    public String getProviderUser() {
        return providerUser;
    }

    public void setProviderUser(String providerUser) {
        this.providerUser = providerUser;

        updateProviderServiceTypeParsableYearMonthDay();
        updateProviderServiceTypeParsableYearMonthDayCustomer();
    }

    public String getCustomerUser() {
        return customerUser;
    }

    public void setCustomerUser(String customerUser) {
        this.customerUser = customerUser;
        updateProviderServiceTypeParsableYearMonthDayCustomer();
    }

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Booking booking = (Booking) o;
        return Objects.equals(providerUser, booking.providerUser) &&
                Objects.equals(customerUser, booking.customerUser) &&
                Objects.equals(serviceType, booking.serviceType) &&
                Objects.equals(date, booking.date) &&
                (Objects.equals(status, booking.status) || Objects.equals(status.isParsable(), booking.status.isParsable()));
    }

    @Override
    public int hashCode() {
        return Objects.hash(providerUser, customerUser, serviceType, status, date);
    }
}
