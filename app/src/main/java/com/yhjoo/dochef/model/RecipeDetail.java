package com.yhjoo.dochef.model;

import com.google.gson.annotations.SerializedName;

public class RecipeDetail {
    @SerializedName("RECIPE_ID")
    private int recipeID;
    @SerializedName("USER_ID")
    private String producerID;
    @SerializedName("NICKNAME")
    private String producerName;
    @SerializedName("TITLE")
    private String title;
    @SerializedName("SUBSTANCE")
    private String substance;
    @SerializedName("THUMBNAIL")
    private String thumbnail;
    @SerializedName("INGREDIENTS")
    private String ingredients;
    @SerializedName("TIME")
    private long dateTime;
    @SerializedName("TAG")
    private String Tag;
    @SerializedName("FILE")
    private String file;

    public String getProducerID() {
        return producerID;
    }

    public String getProducerName() {
        return producerName;
    }

    public int getRecipeID() {
        return recipeID;
    }

    public long getDateTime() {
        return dateTime;
    }

    public String getFile() {
        return file;
    }

    public String getIngredients() {
        return ingredients;
    }

    public String getSubstance() {
        return substance;
    }

    public String getTag() {
        return Tag;
    }

    public String getThumbnail() {
        return thumbnail;
    }

    public String getTitle() {
        return title;
    }
}