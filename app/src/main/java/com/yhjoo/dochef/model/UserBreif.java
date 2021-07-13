package com.yhjoo.dochef.model;

import com.google.gson.annotations.SerializedName;

public class UserBreif {
    @SerializedName("user_id")
    private String userID;
    @SerializedName("profile_img_url")
    private String userImg;
    @SerializedName("nickname")
    private String nickname;
    @SerializedName("is_follow")
    private int is_follow;

    public UserBreif(String userID, String userImg, String nickname, int is_follow) {
        this.userID = userID;
        this.userImg = userImg;
        this.nickname = nickname;
        this.is_follow = is_follow;
    }

    public String getUserID() {
        return userID;
    }

    public int getIs_follow() {
        return is_follow;
    }

    public String getUserImg() {
        return userImg;
    }

    public String getNickname() {
        return nickname;
    }

    @Override
    public String toString() {
        return "UserBreif{" +
                "userID='" + userID + '\'' +
                ", userImg='" + userImg + '\'' +
                ", nickname='" + nickname + '\'' +
                ", is_follow=" + is_follow +
                '}';
    }
}
