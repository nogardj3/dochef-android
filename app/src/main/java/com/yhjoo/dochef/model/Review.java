package com.yhjoo.dochef.model;

import com.google.gson.annotations.SerializedName;

public class Review {
    @SerializedName("review_id")
    private int reviewID;
    @SerializedName("recipe_id")
    private int postID;
    @SerializedName("user_id")
    private String userID;
    @SerializedName("nickname")
    private String nickname;
    @SerializedName("user_profile_img")
    private String userImg;
    @SerializedName("contents")
    private String contents;
    @SerializedName("rating")
    private long rating;
    @SerializedName("datetime")
    private long dateTime;

    public Review(int reviewID, int postID, String userID, String nickname, String userImg, String contents, long rating, long dateTime) {
        this.reviewID = reviewID;
        this.postID = postID;
        this.userID = userID;
        this.nickname = nickname;
        this.userImg = userImg;
        this.contents = contents;
        this.rating = rating;
        this.dateTime = dateTime;
    }

    public int getReviewID() {
        return reviewID;
    }

    public int getPostID() {
        return postID;
    }

    public String getUserID() {
        return userID;
    }

    public String getNickname() {
        return nickname;
    }

    public String getUserImg() {
        return userImg;
    }

    public String getContents() {
        return contents;
    }

    public long getRating() {
        return rating;
    }

    public long getDateTime() {
        return dateTime;
    }

    @Override
    public String toString() {
        return "Review{" +
                "reviewID=" + reviewID +
                ", postID=" + postID +
                ", userID='" + userID + '\'' +
                ", nickname='" + nickname + '\'' +
                ", userImg='" + userImg + '\'' +
                ", contents='" + contents + '\'' +
                ", rating=" + rating +
                ", dateTime=" + dateTime +
                '}';
    }
}