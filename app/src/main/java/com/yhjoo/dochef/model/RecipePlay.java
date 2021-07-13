package com.yhjoo.dochef.model;

import java.io.Serializable;

public class RecipePlay implements Serializable {
    private final int recipeImg;
    private final String title;
    private final String[] ingredients;
    private final String explain;
    private final int dateTime;
    private final String[] tags;

    public RecipePlay(int recipeImg, String title, String[] ingredients, String explain, int dateTime, String[] tags) {
        this.recipeImg = recipeImg;
        this.title = title;
        this.ingredients = ingredients;
        this.explain = explain;
        this.dateTime = dateTime;
        this.tags = tags;
    }

    public int getRecipeImg() {
        return recipeImg;
    }

    public String getTitle() {
        return title;
    }

    public String[] getIngredients() {
        return ingredients;
    }

    public String getExplain() {
        return explain;
    }

    public int getDateTime() {
        return dateTime;
    }

    public String[] getTags() {
        return tags;
    }
}
