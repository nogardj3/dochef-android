package com.yhjoo.dochef.classes;

import com.google.gson.annotations.SerializedName;

import org.json.JSONException;
import org.json.JSONObject;

public class User {
    @SerializedName("USER_ID")
    private String UserID;
    @SerializedName("RECIPE_COUNT")
    private int RecipeCount;
    @SerializedName("FOLLOWER_COUNT")
    private int FollowerCount;
    @SerializedName("FOLLOWING_COUNT")
    private int FollowingCount;
    @SerializedName("IS_FOLLOWING")
    private int FollowingButton;
    @SerializedName("NICKNAME")
    private String Nickname;
    @SerializedName("INTRODUCTION")
    private String ProfileText;
    @SerializedName("PROFILE_IMAGE")
    private String UserImg;

    public User(String userID, int recipeCount, int followerCount, int followingCount, int followingButton, String nickname, String profileText) {
        UserID = userID;
        RecipeCount = recipeCount;
        FollowerCount = followerCount;
        FollowingCount = followingCount;
        FollowingButton = followingButton;
        Nickname = nickname;
        ProfileText = profileText;
    }

    public User(JSONObject userInfo) {
        try {
            UserID = userInfo.getString("USER_ID");
            Nickname = userInfo.getString("NICKNAME");
            ProfileText = userInfo.getString("INTRODUCTION");
            UserImg = userInfo.getString("PROFILE_IMAGE");
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public String getUserID() {
        return UserID;
    }

    public int getRecipeCount() {
        return RecipeCount;
    }

    public int getFollowerCount() {
        return FollowerCount;
    }

    public int getFollowingCount() {
        return FollowingCount;
    }

    public int getFollowingButton() {
        return FollowingButton;
    }

    public void setFollowingButton(int followingButton) {
        FollowingButton = followingButton;
    }

    public String getNickname() {
        return Nickname;
    }

    public String getProfileText() {
        return ProfileText;
    }

    public String getUserImg() {
        return UserImg;
    }
}
