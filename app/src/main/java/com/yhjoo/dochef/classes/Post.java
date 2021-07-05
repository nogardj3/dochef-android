package com.yhjoo.dochef.classes;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class Post implements Serializable {
    @SerializedName("NICKNAME")
    private final String Nickname;
    @SerializedName("PROFILE_IMAGE")
    private final String UserImg;
    @SerializedName("IMAGE")
    private final String PostImg;
    @SerializedName("SUBSTANCE")
    private final String Contents;
    private final int LikeCount;
    private final String[] Tags;
    @SerializedName("POST_ID")
    private int postID;
    @SerializedName("USER_ID")
    private String UserID;

    public Post(String nickname, String userImg, String postImg, int likeCount, String contents, String[] tags) {
        Nickname = nickname;
        UserImg = userImg;
        PostImg = postImg;
        LikeCount = likeCount;
        Contents = contents;
        Tags = tags;
    }

    public int getPostID() {
        return postID;
    }

    public String getUserID() {
        return UserID;
    }

    public String getNickname() {
        return Nickname;
    }

    public String getUserImg() {
        return UserImg;
    }

    public String getPostImg() {
        return PostImg;
    }

    public int getLikeCount() {
        return LikeCount;
    }

    public String getContents() {
        return Contents;
    }

    public String[] getTags() {
        return Tags;
    }
}