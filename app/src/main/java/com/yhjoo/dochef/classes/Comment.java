package com.yhjoo.dochef.classes;

import com.google.gson.annotations.SerializedName;

public class Comment {
    @SerializedName("NICKNAME")
    private final String nickName;
    @SerializedName("COMMENT")
    private final String contents;
    @SerializedName("TIME")
    private final String dateTime;
    @SerializedName("USER_ID")
    private final String userID;
    @SerializedName("COMMENT_ID")
    private int CommentID;
    @SerializedName("RECIPE_ID")
    private int ReciepeID;

    public Comment(String userID, String nickName, String contents, String dateTime) {
        this.userID = userID;
        this.nickName = nickName;
        this.contents = contents;
        this.dateTime = dateTime;
    }

    public String getUserID() {
        return userID;
    }

    public String getNickName() {
        return nickName;
    }

    public String getContents() {
        return contents;
    }

    public String getDateTime() {
        return dateTime;
    }
}
