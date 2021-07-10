package com.yhjoo.dochef.model;

import com.google.gson.annotations.SerializedName;

public class Comment {
    @SerializedName("comment_id")
    private int commentID;
    @SerializedName("post_id")
    private int postID;
    @SerializedName("user_id")
    private String userID;
    @SerializedName("nickname")
    private String nickName;
    @SerializedName("profile_img_url")
    private String userImg;
    @SerializedName("contents")
    private String contents;
    @SerializedName("datetime")
    private long dateTime;

    // Detail
    public Comment(int commentID, int postID, String userID, String nickName, String userImg, String contents, long dateTime) {
        this.commentID = commentID;
        this.postID = postID;
        this.userID = userID;
        this.nickName = nickName;
        this.userImg = userImg;
        this.contents = contents;
        this.dateTime = dateTime;
    }

    // Create
    public Comment(int postID, String userID, String contents, long dateTime) {
        this.postID = postID;
        this.userID = userID;
        this.contents = contents;
        this.dateTime = dateTime;
    }

    // Dummy
    public Comment(String nickName, String contents, long dateTime) {
        this.nickName = nickName;
        this.contents = contents;
        this.dateTime = dateTime;
    }

    public int getCommentID() {
        return commentID;
    }

    public int getPostID() {
        return postID;
    }

    public String getUserID() {
        return userID;
    }

    public String getNickName() {
        return nickName;
    }

    public String getUserImg() {
        return userImg;
    }

    public String getContents() {
        return contents;
    }

    public long getDateTime() {
        return dateTime;
    }
}
