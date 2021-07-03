package com.yhjoo.dochef.classes;

public class Review {
    private final String ImageURL;
    private final String UserID;
    private final String UserImg;
    private final String Nickname;
    private final String Contents;
    private final long Date;
    private final int Rating;

    public Review(String imageURL, String userID, String userImg, String nickname, String contents, long date, int rating) {
        ImageURL = imageURL;
        UserID = userID;
        UserImg = userImg;
        Nickname = nickname;
        Contents = contents;
        Date = date;
        Rating = rating;
    }

    public int getRating() {
        return Rating;
    }

    public String getImageURL() {
        return ImageURL;
    }

    public String getUserID() {
        return UserID;
    }

    public String getUserImg() {
        return UserImg;
    }

    public String getNickname() {
        return Nickname;
    }

    public String getContents() {
        return Contents;
    }

    public long getDate() {
        return Date;
    }
}