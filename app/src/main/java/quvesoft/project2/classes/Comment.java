package quvesoft.project2.classes;

import com.google.gson.annotations.SerializedName;

public class Comment {
    @SerializedName("COMMENT_ID")
    private int CommentID;
    @SerializedName("RECIPE_ID")
    private int ReciepeID;
    @SerializedName("USER_ID")
    private String UserID;
    @SerializedName("NICKNAME")
    private String NickName;
    @SerializedName("COMMENT")
    private String Contents;
    @SerializedName("TIME")
    private String Date;

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
