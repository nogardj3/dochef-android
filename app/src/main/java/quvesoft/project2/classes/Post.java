package quvesoft.project2.classes;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class Post implements Serializable {
    @SerializedName("POST_ID")
    private int postID;
    @SerializedName("USER_ID")
    private String UserID;
    @SerializedName("NICKNAME")
    private String Nickname;
    @SerializedName("PROFILE_IMAGE")
    private String UserImg;
    @SerializedName("IMAGE")
    private String PostImg;
    @SerializedName("SUBSTANCE")
    private String Contents;

    private int LikeCount;
    private String[] Tags;

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