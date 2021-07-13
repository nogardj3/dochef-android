package com.yhjoo.dochef.model;

import com.google.gson.annotations.SerializedName;

import java.util.Arrays;

public class RecipeBrief {
    @SerializedName("recipe_id")
    private int recipeID;
    @SerializedName("user_id")
    private String userID;
    @SerializedName("nickname")
    private String nickname;
    @SerializedName("profile_img_url")
    private String userImg;
    @SerializedName("recipe_name")
    private int recipeName;
    @SerializedName("recipe_img")
    private String recipeImg;
    @SerializedName("datetime")
    private long datetime;
    @SerializedName("view_count")
    private int view_count;
    @SerializedName("rating")
    private int rating;
    @SerializedName("ingredients")
    private Ingredient ingredients;
    @SerializedName("tags")
    private String[] tags;

    public RecipeBrief(int recipeID, String userID, String nickname, String userImg, int recipeName, String recipeImg, long datetime, int view_count, int rating, Ingredient ingredients, String[] tags) {
        this.recipeID = recipeID;
        this.userID = userID;
        this.nickname = nickname;
        this.userImg = userImg;
        this.recipeName = recipeName;
        this.recipeImg = recipeImg;
        this.datetime = datetime;
        this.view_count = view_count;
        this.rating = rating;
        this.ingredients = ingredients;
        this.tags = tags;
    }

    public int getRecipeID() {
        return recipeID;
    }

    public String getUserID() {
        return userID;
    }

    public String getNickname() {
        return nickname;
    }

    public String getUserImg() {
        return userImg;
    }

    public int getRecipeName() {
        return recipeName;
    }

    public String getRecipeImg() {
        return recipeImg;
    }

    public long getDatetime() {
        return datetime;
    }

    public int getView_count() {
        return view_count;
    }

    public int getRating() {
        return rating;
    }

    public Ingredient getIngredients() {
        return ingredients;
    }

    public String[] getTags() {
        return tags;
    }

    @Override
    public String toString() {
        return "RecipeBrief{" +
                "recipeID=" + recipeID +
                ", userID='" + userID + '\'' +
                ", nickname='" + nickname + '\'' +
                ", userImg='" + userImg + '\'' +
                ", recipeName=" + recipeName +
                ", recipeImg='" + recipeImg + '\'' +
                ", datetime=" + datetime +
                ", view_count=" + view_count +
                ", rating=" + rating +
                ", ingredients=" + ingredients +
                ", tags=" + Arrays.toString(tags) +
                '}';
    }
}
