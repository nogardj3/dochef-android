package com.yhjoo.dochef.model;

import com.google.gson.annotations.SerializedName;

public class UserBreif {
    @SerializedName("user_id")
    private final String userID;
    @SerializedName("profile_img_url")
    private final String userImg;
    @SerializedName("nickname")
    private final String nickname;
    @SerializedName("is_follow")
    private final int is_follow;
    /*

     */

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
}
