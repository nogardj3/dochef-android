package com.yhjoo.dochef.model;

import com.google.gson.annotations.SerializedName;

public class PostComment {
    @SerializedName("COMMENT_ID")
    private int commentID;
    @SerializedName("POST_ID")
    private int reciepeID;
    @SerializedName("USER_ID")
    private String userID;
    @SerializedName("NICKNAME")
    private String nickName;
    @SerializedName("PROFILE_IMAGE")
    private String userImg;
    @SerializedName("COMMENT")
    private String contents;
    @SerializedName("COMMENT_TIME")
    private long dateTime;

    public String getUserID() {
        return userID;
    }

    public String getNickName() {
        return nickName;
    }

    public void setNickName(String nickName) {
        this.nickName = nickName;
    }

    public String getContents() {
        return contents;
    }

    public void setContents(String contents) {
        this.contents = contents;
    }

    public long getDateTime() {
        return dateTime;
    }

    public void setDateTime(long dateTime) {
        this.dateTime = dateTime;
    }
}
