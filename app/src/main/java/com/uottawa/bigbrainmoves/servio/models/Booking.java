package com.uottawa.bigbrainmoves.servio.models;

import com.uottawa.bigbrainmoves.servio.util.enums.BookingStatus;
import com.uottawa.bigbrainmoves.servio.util.enums.DayOfWeek;

public class Booking {
    private String providerUser;
    private String customerUser;
    private String serviceType;
    private String date;
    private String startTime;
    private String endTime;
    private String providerServiceTypeParsableYearMonthDay;
    private String providerServiceTypeParsableDayOfWeek;
    private String providerServiceTypeParsableYearMonthDayCustomer;
    private BookingStatus status;
    private DayOfWeek dayOfWeek;
    private double price;

    public Booking() {}

    public Booking(String providerUser, String customerUser, String serviceType,
                   String date, String startTime, String endTime, double price, BookingStatus status,
                   DayOfWeek dayOfWeek) {

        this.providerUser = providerUser;
        this.customerUser = customerUser;
        this.serviceType = serviceType;
        this.startTime = startTime;
        this.endTime = endTime;
        this.price = price;
        this.status = status;
        this.dayOfWeek = dayOfWeek;
        this.date = date;

        updateProviderServiceTypeParsableDayOfWeek();
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

    public DayOfWeek getDayOfWeek() {
        return dayOfWeek;
    }

    public void setDayOfWeek(DayOfWeek dayOfWeek) {
        this.dayOfWeek = dayOfWeek;
        updateProviderServiceTypeParsableDayOfWeek();
    }

    public String getProviderServiceTypeParsableYearMonthDay() {
        return providerServiceTypeParsableYearMonthDay;
    }

    public String getProviderServiceTypeParsableDayOfWeek() {
        return providerServiceTypeParsableDayOfWeek;
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
        updateProviderServiceTypeParsableDayOfWeek();
        updateProviderServiceTypeParsableYearMonthDayCustomer();
    }


    private void updateProviderServiceTypeParsableYearMonthDay() {
        if (providerUser == null || serviceType == null || status == null || date == null)
            return;
        providerServiceTypeParsableYearMonthDay = providerUser + serviceType +
                status.isParsable() + date;
    }

    private void updateProviderServiceTypeParsableDayOfWeek() {
        if (providerUser == null || serviceType == null || status == null || dayOfWeek == null)
            return;
        providerServiceTypeParsableDayOfWeek = providerUser + serviceType +
                status.isParsable() + dayOfWeek.toString();
    }

    private void updateProviderServiceTypeParsableYearMonthDayCustomer() {
        if (customerUser == null || providerServiceTypeParsableYearMonthDay == null)
            return;

        providerServiceTypeParsableYearMonthDayCustomer = providerServiceTypeParsableYearMonthDay + customerUser;
    }
    public void setStatus(BookingStatus status) {
        this.status = status;

        updateProviderServiceTypeParsableDayOfWeek();
        updateProviderServiceTypeParsableYearMonthDay();
        updateProviderServiceTypeParsableYearMonthDayCustomer();

    }

    public String getProviderUser() {
        return providerUser;
    }

    public void setProviderUser(String providerUser) {
        this.providerUser = providerUser;

        updateProviderServiceTypeParsableDayOfWeek();
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
}
