package com.bitm.tourmate.Model_Class;

public class User   {

    private String firstName;
    private String lastName;
    private String email;
    private String picture;
    private String uid;

    public User() {
    }

    public User(String firstName, String lastName, String email, String picture, String uid) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.picture = picture;
        this.uid = uid;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getEmail() {
        return email;
    }

    public String getPicture() {
        return picture;
    }
}
