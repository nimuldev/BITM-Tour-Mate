package com.bitm.tourmate.Model_Class;

public class Trip {

    private String tripName;
    private String description;
    private String endDate;
    private String startDate;
    private String tripId;

    public Trip() {
    }

    public Trip(String tripName, String description, String endDate, String startDate, String tripId) {
        this.tripName = tripName;
        this.description = description;
        this.endDate = endDate;
        this.startDate = startDate;
        this.tripId = tripId;
    }

    public String getTripName() {
        return tripName;
    }

    public String getDescription() {
        return description;
    }

    public String getEndDate() {
        return endDate;
    }

    public String getStartDate() {
        return startDate;
    }

    public String getTripId() {
        return tripId;
    }
}
