package com.yhjoo.dochef.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;

public class Post implements Serializable {
    @SerializedName("post_id")
    private int postID;
    @SerializedName("user_id")
    private String UserID;
    @SerializedName("nickname")
    private String nickname;
    @SerializedName("profile_img_url")
    private String userImg;
    @SerializedName("post_img")
    private ArrayList<String> postImg;
    @SerializedName("datetime")
    private long dateTime;
    @SerializedName("contents")
    private String contents;
    @SerializedName("tags")
    private ArrayList<String> tags;
    @SerializedName("comments")
    private ArrayList<Comment> comments;
    @SerializedName("like_count")
    private Integer like_count;

    // Detail

    public Post(int postID, String userID, String nickname, String userImg, ArrayList<String> postImg, long dateTime, String contents, ArrayList<String> tags, ArrayList<Comment> comments, Integer like_count) {
        this.postID = postID;
        UserID = userID;
        this.nickname = nickname;
        this.userImg = userImg;
        this.postImg = postImg;
        this.dateTime = dateTime;
        this.contents = contents;
        this.tags = tags;
        this.comments = comments;
        this.like_count = like_count;
    }


    // Dummy


    public int getPostID() {
        return postID;
    }

    public String getUserID() {
        return UserID;
    }

    public String getNickname() {
        return nickname;
    }

    public String getUserImg() {
        return userImg;
    }

    public ArrayList<String> getPostImg() {
        return postImg;
    }

    public long getDateTime() {
        return dateTime;
    }

    public String getContents() {
        return contents;
    }

    public ArrayList<Comment> getComments() {
        return comments;
    }

    public Integer getLike_count() {
        return like_count;
    }

    public ArrayList<String> getTags() {
        return tags;
    }
}