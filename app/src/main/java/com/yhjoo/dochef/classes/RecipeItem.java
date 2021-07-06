package com.yhjoo.dochef.classes;

import java.io.Serializable;

public class RecipeItem implements Serializable {
    private final int recipeType;
    private final int recipeImg;
    private final String title;
    private final String[] ingredients;
    private final String explain;
    private final int dateTime;
    private final String[] tags;

    public RecipeItem(int recipeType, int recipeImg, String title, String[] ingredients, String explain, int dateTime, String[] tags) {
        this.recipeType = recipeType;
        this.recipeImg = recipeImg;
        this.title = title;
        this.ingredients = ingredients;
        this.explain = explain;
        this.dateTime = dateTime;
        this.tags = tags;
    }

    public int getRecipeType() {
        return recipeType;
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
