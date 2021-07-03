package com.yhjoo.dochef.classes;

import com.google.gson.annotations.SerializedName;

public class UserList {
    @SerializedName("USER_ID")
    private final String UserID;
    @SerializedName("PROFILE_IMAGE")
    private final String UserImg;
    @SerializedName("NICKNAME")
    private final String Nickname;

    public UserList(String userID, String userImg, String nickname) {
        UserID = userID;
        UserImg = userImg;
        Nickname = nickname;
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
}
