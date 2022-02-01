package com.pmkisanyojnastatusdetail.models;

public class StatusModel {
    private final String id;
    private final String userId;
    private final String image;
    private final String time;
    private final String profileID;
    private final String profileImage;
    private final String profileName;

    public StatusModel(String id, String userId, String image, String time, String profileID, String profileImage, String profileName) {
        this.id = id;
        this.userId = userId;
        this.image = image;
        this.time = time;
        this.profileID = profileID;
        this.profileImage = profileImage;
        this.profileName = profileName;
    }

    public String getId() {
        return id;
    }

    public String getUserId() {
        return userId;
    }

    public String getImage() {
        return image;
    }

    public String getTime() {
        return time;
    }

    public String getProfileID() {
        return profileID;
    }

    public String getProfileImage() {
        return profileImage;
    }

    public String getProfileName() {
        return profileName;
    }
}
