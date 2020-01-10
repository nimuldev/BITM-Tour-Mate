package com.bitm.tourmate.Model_Class;

public class Expanse {

    private String amount;
    private String date;
    private String expanceType;
    private String description;
    private String time;
    private String expenseId;
    private String tripId;

    public Expanse() {
    }

    public Expanse(String amount, String date, String expanceType, String description, String time, String expenseId, String tripId) {
        this.amount = amount;
        this.date = date;
        this.expanceType = expanceType;
        this.description = description;
        this.time = time;
        this.expenseId = expenseId;
        this.tripId = tripId;
    }

    public String getAmount() {
        return amount;
    }

    public String getDate() {
        return date;
    }

    public String getExpanceType() {
        return expanceType;
    }

    public String getDescription() {
        return description;
    }

    public String getTime() {
        return time;
    }

    public String getExpenseId() {
        return expenseId;
    }

    public String getTripId() {
        return tripId;
    }
}