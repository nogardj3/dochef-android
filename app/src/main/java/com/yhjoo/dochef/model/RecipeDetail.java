package com.yhjoo.dochef.model;

import com.google.gson.annotations.SerializedName;

import java.util.Arrays;

public class RecipeDetail {
    @SerializedName("recipe_id")
    private int recipeID;
    @SerializedName("recipe_name")
    private int recipeName;
    @SerializedName("user_id")
    private String userID;
    @SerializedName("nickname")
    private String nickname;
    @SerializedName("profile_img_url")
    private String userImg;
    @SerializedName("recipe_img")
    private String recipeImg;
    @SerializedName("contents")
    private String contents;
    @SerializedName("datetime")
    private long datetime;
    @SerializedName("amount_time")
    private String amount_time;
    @SerializedName("view_count")
    private int view_count;
    @SerializedName("rating")
    private int rating;
    @SerializedName("ingredients")
    private Ingredient ingredient;
    @SerializedName("tags")
    private String[] tags;
    @SerializedName("phase")
    private RecipePhase phases;

    public Recipe(int recipeID, int recipeName, String userID, String nickname, String userImg, String recipeImg, String contents, long datetime, String amount_time, int view_count, int rating, Ingredient ingredient, String[] tags, RecipePhase phases) {
        this.recipeID = recipeID;
        this.recipeName = recipeName;
        this.userID = userID;
        this.nickname = nickname;
        this.userImg = userImg;
        this.recipeImg = recipeImg;
        this.contents = contents;
        this.datetime = datetime;
        this.amount_time = amount_time;
        this.view_count = view_count;
        this.rating = rating;
        this.ingredient = ingredient;
        this.tags = tags;
        this.phases = phases;
    }

    public int getRecipeID() {
        return recipeID;
    }

    public int getRecipeName() {
        return recipeName;
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

    public String getRecipeImg() {
        return recipeImg;
    }

    public String getContents() {
        return contents;
    }

    public long getDatetime() {
        return datetime;
    }

    public String getAmount_time() {
        return amount_time;
    }

    public int getView_count() {
        return view_count;
    }

    public int getRating() {
        return rating;
    }

    public Ingredient getIngredient() {
        return ingredient;
    }

    public String[] getTags() {
        return tags;
    }

    public RecipePhase getPhases() {
        return phases;
    }

    @Override
    public String toString() {
        return "Recipe{" +
                "recipeID=" + recipeID +
                ", recipeName=" + recipeName +
                ", userID='" + userID + '\'' +
                ", nickname='" + nickname + '\'' +
                ", userImg='" + userImg + '\'' +
                ", recipeImg='" + recipeImg + '\'' +
                ", contents='" + contents + '\'' +
                ", datetime=" + datetime +
                ", amount_time='" + amount_time + '\'' +
                ", view_count=" + view_count +
                ", rating=" + rating +
                ", ingredient=" + ingredient +
                ", tags=" + Arrays.toString(tags) +
                ", phases=" + phases +
                '}';
    }
}