package com.bitm.tourmate.Model_Class;

public class Memory {
    private String caption;
    private String picture;
    private String tripId;
    private String memoreyId;

    public Memory() {
    }

    public Memory(String caption, String picture, String tripId, String memoreyId) {
        this.caption = caption;
        this.picture = picture;
        this.tripId = tripId;
        this.memoreyId = memoreyId;
    }

    public String getCaption() {
        return caption;
    }

    public String getPicture() {
        return picture;
    }

    public String getTripId() {
        return tripId;
    }

    public String getMemoreyId() {
        return memoreyId;
    }
}
