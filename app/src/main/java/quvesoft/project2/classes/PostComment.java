package quvesoft.project2.classes;

import com.google.gson.annotations.SerializedName;

public class PostComment {
    @SerializedName("COMMENT_ID")
    private int CommentID;
    @SerializedName("POST_ID")
    private int ReciepeID;
    @SerializedName("USER_ID")
    private String UserID;
    @SerializedName("NICKNAME")
    private String NickName;
    @SerializedName("PROFILE_IMAGE")
    private String UserImg;
    @SerializedName("COMMENT")
    private String Contents;
    @SerializedName("COMMENT_TIME")
    private long Date;

    public String getUserID() {
        return UserID;
    }

    public String getNickName() {
        return NickName;
    }

    public String getContents() {
        return Contents;
    }

    public long getDate() {
        return Date;
    }

    public void setNickName(String nickName) {
        NickName = nickName;
    }

    public void setContents(String contents) {
        Contents = contents;
    }

    public void setDate(long date) {
        Date = date;
    }
}
