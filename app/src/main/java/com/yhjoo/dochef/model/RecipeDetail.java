package com.yhjoo.dochef.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;

public class RecipeDetail implements Serializable {
    @SerializedName("recipe_id")
    private int recipeID;
    @SerializedName("recipe_name")
    private String recipeName;
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
    @SerializedName("likes")
    private ArrayList<String> likes;
    @SerializedName("rating")
    private int rating;
    @SerializedName("ingredients")
    private ArrayList<Ingredient> ingredients;
    @SerializedName("tags")
    private ArrayList<String> tags;
    @SerializedName("phase")
    private ArrayList<RecipePhase> phases;

    public RecipeDetail(int recipeID, String recipeName, String userID, String nickname, String userImg, String recipeImg, String contents, long datetime, String amount_time, int view_count, ArrayList<String> likes, int rating, ArrayList<Ingredient> ingredients, ArrayList<String> tags, ArrayList<RecipePhase> phases) {
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
        this.likes = likes;
        this.rating = rating;
        this.ingredients = ingredients;
        this.tags = tags;
        this.phases = phases;
    }

    public int getRecipeID() {
        return recipeID;
    }

    public String getRecipeName() {
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

    public ArrayList<String> getLikes() {
        return likes;
    }

    public int getRating() {
        return rating;
    }

    public ArrayList<Ingredient> getIngredients() {
        return ingredients;
    }

    public ArrayList<String> getTags() {
        return tags;
    }

    public ArrayList<RecipePhase> getPhases() {
        return phases;
    }

    @Override
    public String toString() {
        return "RecipeDetail{" +
                "recipeID=" + recipeID +
                ", recipeName='" + recipeName + '\'' +
                ", userID='" + userID + '\'' +
                ", nickname='" + nickname + '\'' +
                ", userImg='" + userImg + '\'' +
                ", recipeImg='" + recipeImg + '\'' +
                ", contents='" + contents + '\'' +
                ", datetime=" + datetime +
                ", amount_time='" + amount_time + '\'' +
                ", view_count=" + view_count +
                ", likes=" + likes +
                ", rating=" + rating +
                ", ingredients=" + ingredients +
                ", tags=" + tags +
                ", phases=" + phases +
                '}';
    }
}