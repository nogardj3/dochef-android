package com.yhjoo.dochef.model;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class UserBrief {
    @SerializedName("user_id")
    private String userID;
    @SerializedName("profile_img_url")
    private String userImg;
    @SerializedName("nickname")
    private String nickname;
    @SerializedName("follow")
    private ArrayList<String> follow;

    public UserBrief(String userID, String userImg, String nickname, ArrayList<String> follow) {
        this.userID = userID;
        this.userImg = userImg;
        this.nickname = nickname;
        this.follow = follow;
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

    @Override
    public String toString() {
        return "UserBrief{" +
                "userID='" + userID + '\'' +
                ", userImg='" + userImg + '\'' +
                ", nickname='" + nickname + '\'' +
                ", follow=" + follow +
                '}';
    }
}
