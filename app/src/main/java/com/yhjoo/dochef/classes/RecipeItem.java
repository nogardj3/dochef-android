package com.yhjoo.dochef.classes;

import java.io.Serializable;

public class RecipeItem implements Serializable {
    private final int TYPE;
    private final int RecipeImg;
    private final String Title;
    private final String[] Ingredients;
    private final String Explain;
    private final int Date;
    private final String[] Tags;

    public RecipeItem(int TYPE, int recipeImg, String title, String[] ingredients, String explain, int date, String[] tags) {
        this.TYPE = TYPE;
        RecipeImg = recipeImg;
        Title = title;
        Ingredients = ingredients;
        Explain = explain;
        Date = date;
        Tags = tags;
    }

    public int getTYPE() {
        return TYPE;
    }

    public int getRecipeImg() {
        return RecipeImg;
    }

    public String getTitle() {
        return Title;
    }

    public String[] getIngredients() {
        return Ingredients;
    }

    public String getExplain() {
        return Explain;
    }

    public int getDate() {
        return Date;
    }

    public String[] getTags() {
        return Tags;
    }
}
