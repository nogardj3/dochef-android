package com.yhjoo.dochef.model;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class UserBrief {
    @SerializedName("user_id")
    private String userID;
    @SerializedName("user_profile_img")
    private String userImg;
    @SerializedName("nickname")
    private String nickname;
    @SerializedName("follow")
    private ArrayList<String> follow;
    @SerializedName("follower_count")
    private int follower_count;

    public UserBrief(String userID, String userImg, String nickname, ArrayList<String> follow, int follower_count) {
        this.userID = userID;
        this.userImg = userImg;
        this.nickname = nickname;
        this.follow = follow;
        this.follower_count = follower_count;
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

    public ArrayList<String> getFollow() {
        return follow;
    }

    public int getFollower_count() {
        return follower_count;
    }

    @Override
    public String toString() {
        return "UserBrief{" +
                "userID='" + userID + '\'' +
                ", userImg='" + userImg + '\'' +
                ", nickname='" + nickname + '\'' +
                ", follow=" + follow +
                ", follower_count=" + follower_count +
                '}';
    }
}
