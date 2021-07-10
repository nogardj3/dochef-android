package com.yhjoo.dochef.model;

import com.google.gson.annotations.SerializedName;

import org.json.JSONException;
import org.json.JSONObject;

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

    public UserDetail(String userID, int recipeCount, int followerCount, int followingCount, int isfollowing, String nickname, String profileText) {
        this.userID = userID;
        this.recipeCount = recipeCount;
        this.followerCount = followerCount;
        this.followingCount = followingCount;
        is_following = isfollowing;
        this.nickname = nickname;
        this.profileText = profileText;
    }

    public UserDetail(JSONObject userInfo) {
        try {
            userID = userInfo.getString("USER_ID");
            nickname = userInfo.getString("NICKNAME");
            profileText = userInfo.getString("INTRODUCTION");
            userImg = userInfo.getString("PROFILE_IMAGE");
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public String getUserID() {
        return userID;
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

    public String getNickname() {
        return nickname;
    }

    public String getProfileText() {
        return profileText;
    }

    public String getUserImg() {
        return userImg;
    }
}
