package com.yhjoo.dochef.model;

import com.google.gson.annotations.SerializedName;

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
    @SerializedName("follower_count")
    private int followerCount;
    @SerializedName("following_count")
    private int followingCount;
    @SerializedName("is_follow")
    private int is_following;

    public UserDetail(String userID, String userImg, String nickname, String profileText, int recipeCount, int followerCount, int followingCount, int is_following) {
        this.userID = userID;
        this.userImg = userImg;
        this.nickname = nickname;
        this.profileText = profileText;
        this.recipeCount = recipeCount;
        this.followerCount = followerCount;
        this.followingCount = followingCount;
        this.is_following = is_following;
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

    public int getFollowerCount() {
        return followerCount;
    }

    public int getFollowingCount() {
        return followingCount;
    }

    public int getIs_following() {
        return is_following;
    }
}
