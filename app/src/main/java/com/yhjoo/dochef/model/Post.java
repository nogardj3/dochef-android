package com.yhjoo.dochef.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class Post implements Serializable {
    @SerializedName("NICKNAME")
    private final String nickName;
    @SerializedName("PROFILE_IMAGE")
    private final String userImg;
    @SerializedName("IMAGE")
    private final String postImg;
    @SerializedName("SUBSTANCE")
    private final String contents;
    private final int likeCount;
    private final String[] tags;
    @SerializedName("POST_ID")
    private int postID;
    @SerializedName("USER_ID")
    private String UserID;

    public Post(String nickname, String userImg, String postImg, int likeCount, String contents, String[] tags) {
        this.nickName = nickname;
        this.userImg = userImg;
        this.postImg = postImg;
        this.likeCount = likeCount;
        this.contents = contents;
        this.tags = tags;
    }

    public int getPostID() {
        return postID;
    }

    public String getUserID() {
        return UserID;
    }

    public String getNickName() {
        return nickName;
    }

    public String getUserImg() {
        return userImg;
    }

    public String getPostImg() {
        return postImg;
    }

    public int getLikeCount() {
        return likeCount;
    }

    public String getContents() {
        return contents;
    }

    public String[] getTags() {
        return tags;
    }
}