package com.yhjoo.dochef.classes;

public class Review {
    private final String imageURL;
    private final String userID;
    private final String userImg;
    private final String nickname;
    private final String contents;
    private final long dateTime;
    private final int rating;

    public Review(String imageURL, String userID, String userImg, String nickname, String contents, long dateTime, int rating) {
        this.imageURL = imageURL;
        this.userID = userID;
        this.userImg = userImg;
        this.nickname = nickname;
        this.contents = contents;
        this.dateTime = dateTime;
        this.rating = rating;
    }

    public int getRating() {
        return rating;
    }

    public String getImageURL() {
        return imageURL;
    }

    public String getUserID() {
        return userID;
    }

    public String getUserImg() {
        return userImg;
    }

    public String getNickname() {
        return nickname;
    }

    public String getContents() {
        return contents;
    }

    public long getDateTime() {
        return dateTime;
    }
}