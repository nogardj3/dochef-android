package com.yhjoo.dochef.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;

public class Post implements Serializable {
    @SerializedName("post_id")
    private int postID;
    @SerializedName("user_id")
    private String UserID;
    @SerializedName("post_img")
    private String postImg;
    @SerializedName("contents")
    private String contents;
    @SerializedName("datetime")
    private long dateTime;
    @SerializedName("tags")
    private ArrayList<String> tags;
    @SerializedName("comments")
    private ArrayList<Comment> comments;
    @SerializedName("likes")
    private ArrayList<String> likes;
    @SerializedName("nickname")
    private String nickname;
    @SerializedName("profile_img_url")
    private String userImg;

    public Post(int postID, String userID, String nickname, String userImg, String postImg, long dateTime, String contents, ArrayList<String> tags, ArrayList<Comment> comments, ArrayList<String> likes) {
        this.postID = postID;
        UserID = userID;
        this.nickname = nickname;
        this.userImg = userImg;
        this.postImg = postImg;
        this.dateTime = dateTime;
        this.contents = contents;
        this.tags = tags;
        this.comments = comments;
        this.likes = likes;
    }

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

    public String getPostImg() {
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

    public ArrayList<String> getLikes() {
        return likes;
    }

    public ArrayList<String> getTags() {
        return tags;
    }



    @Override
    public String toString() {
        return "Post{" +
                "postID=" + postID +
                ", UserID='" + UserID + '\'' +
                ", postImg='" + postImg + '\'' +
                ", contents='" + contents + '\'' +
                ", dateTime=" + dateTime +
                ", tags=" + tags +
                ", comments=" + comments +
                ", likes=" + likes +
                ", nickname='" + nickname + '\'' +
                ", userImg='" + userImg + '\'' +
                '}';
    }
}