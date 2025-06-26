package com.example.quotion.ui.profile;

public class UserProfile {
    private String name;
    private String email;
    private String photoUrl;

    public UserProfile(String name, String email, String photoUrl) {
        this.name = name;
        this.email = email;
        this.photoUrl = photoUrl;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public String getPhotoUrl() {
        return photoUrl;
    }
}
