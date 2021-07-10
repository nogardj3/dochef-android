package com.yhjoo.dochef.model;

import com.google.gson.annotations.SerializedName;

import org.json.JSONException;
import org.json.JSONObject;

public class UserDetail {
    @SerializedName("user_id")
    private String userID;
    @SerializedName("PROFILE_IMAGE")
    private String userImg;
    @SerializedName("NICKNAME")
    private String nickname;
    @SerializedName("INTRODUCTION")
    private String profileText;
    @SerializedName("RECIPE_COUNT")
    private int recipeCount;
    @SerializedName("FOLLOWER_COUNT")
    private int followerCount;
    @SerializedName("FOLLOWING_COUNT")
    private int followingCount;
    @SerializedName("IS_FOLLOWING")
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

    public int getIs_following() {
        return is_following;
    }

    public void setIs_following(int is_following) {
        this.is_following = is_following;
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
