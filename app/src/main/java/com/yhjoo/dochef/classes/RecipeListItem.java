package com.yhjoo.dochef.classes;

import java.util.ArrayList;

public class RecipeListItem {
    private String Title;
    private String NickName;
    private String Explain;
    private int ViewsCount;
    private String RecipeImg;
    private ArrayList<String> Ingredients;
    private ArrayList<String> Tags;

    public RecipeListItem(String title, String nickName, String explain, int viewsCount, String recipeImg, ArrayList<String> ingredients, ArrayList<String> tags) {
        Title = title;
        NickName = nickName;
        Explain = explain;
        ViewsCount = viewsCount;
        RecipeImg = recipeImg;
        Ingredients = ingredients;
        Tags = tags;
    }

    public RecipeListItem(String title, String nickName, String explain, int viewsCount, String recipeImg) {
        Title = title;
        NickName = nickName;
        Explain = explain;
        ViewsCount = viewsCount;
        RecipeImg = recipeImg;
    }

    public ArrayList<String> getIngredients() {
        return Ingredients;
    }

    public void setIngredients(ArrayList<String> ingredients) {
        Ingredients = ingredients;
    }

    public ArrayList<String> getTags() {
        return Tags;
    }

    public void setTags(ArrayList<String> tags) {
        Tags = tags;
    }

    public String getTitle() {
        return Title;
    }

    public void setTitle(String title) {
        Title = title;
    }

    public String getNickName() {
        return NickName;
    }

    public void setNickName(String nickName) {
        NickName = nickName;
    }

    public String getExplain() {
        return Explain;
    }

    public void setExplain(String explain) {
        Explain = explain;
    }

    public int getViewsCount() {
        return ViewsCount;
    }

    public void setViewsCount(int viewsCount) {
        ViewsCount = viewsCount;
    }

    public String getRecipeImg() {
        return RecipeImg;
    }

    public void setRecipeImg(String recipeImg) {
        RecipeImg = recipeImg;
    }
}
