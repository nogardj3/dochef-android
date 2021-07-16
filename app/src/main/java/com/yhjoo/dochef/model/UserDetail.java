package com.yhjoo.dochef.model;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class UserDetail {
    @SerializedName("user_id")
    private String userID;
    @SerializedName("profile_img_url")
    private String userImg;
    @SerializedName("nickname")
    private String nickname;
    @SerializedName("bio")
    private String profileText;
    @SerializedName("recipe_count")
    private int recipeCount;
    @SerializedName("follow")
    private ArrayList<String> follow;
    @SerializedName("follower_count")
    private int followerCount;
    @SerializedName("following_count")
    private int followingCount;

    public UserDetail(String userID, String userImg, String nickname, String profileText, int recipeCount, ArrayList<String> follow, int followerCount, int followingCount) {
        this.userID = userID;
        this.userImg = userImg;
        this.nickname = nickname;
        this.profileText = profileText;
        this.recipeCount = recipeCount;
        this.follow = follow;
        this.followerCount = followerCount;
        this.followingCount = followingCount;
    }

    public String getUserID() {
        return userID;
    }

    public String getUserImg() {
        return userImg;
    }

    public String getNickname() {
        return nickname;
    }

    public String getProfileText() {
        return profileText;
    }

    public int getRecipeCount() {
        return recipeCount;
    }

    public ArrayList<String> getFollow() {
        return follow;
    }

    public int getFollowerCount() {
        return followerCount;
    }

    public int getFollowingCount() {
        return followingCount;
    }

    @Override
    public String toString() {
        return "UserDetail{" +
                "userID='" + userID + '\'' +
                ", userImg='" + userImg + '\'' +
                ", nickname='" + nickname + '\'' +
                ", profileText='" + profileText + '\'' +
                ", recipeCount=" + recipeCount +
                ", follow=" + follow +
                ", followerCount=" + followerCount +
                ", followingCount=" + followingCount +
                '}';
    }
}
