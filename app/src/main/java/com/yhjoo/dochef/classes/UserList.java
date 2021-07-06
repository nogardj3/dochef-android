package com.yhjoo.dochef.classes;

import com.google.gson.annotations.SerializedName;

public class UserList {
    @SerializedName("USER_ID")
    private final String userID;
    @SerializedName("PROFILE_IMAGE")
    private final String userImg;
    @SerializedName("NICKNAME")
    private final String nickname;

    public UserList(String userID, String userImg, String nickname) {
        this.userID = userID;
        this.userImg = userImg;
        this.nickname = nickname;
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
}
