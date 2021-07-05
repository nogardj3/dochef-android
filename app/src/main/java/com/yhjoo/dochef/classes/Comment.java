package com.yhjoo.dochef.classes;

import com.google.gson.annotations.SerializedName;

public class Comment {
    @SerializedName("NICKNAME")
    private final String NickName;
    @SerializedName("COMMENT")
    private final String Contents;
    @SerializedName("TIME")
    private final String Date;
    @SerializedName("COMMENT_ID")
    private int CommentID;
    @SerializedName("RECIPE_ID")
    private int ReciepeID;
    @SerializedName("USER_ID")
    private String UserID;

    public Comment(String nickName, String contents, String date) {
        NickName = nickName;
        Contents = contents;
        Date = date;
    }

    public String getUserID() {
        return UserID;
    }

    public String getNickName() {
        return NickName;
    }

    public String getContents() {
        return Contents;
    }

    public String getDate() {
        return Date;
    }
}
