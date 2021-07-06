package com.yhjoo.dochef.classes;

import java.util.ArrayList;

public class RecipeListItem {
    private String title;
    private String nickName;
    private String explain;
    private int viewsCount;
    private String recipeImg;
    private ArrayList<String> ingredients;
    private ArrayList<String> tags;

    public RecipeListItem(String title, String nickName, String explain, int viewsCount, String recipeImg, ArrayList<String> ingredients, ArrayList<String> tags) {
        this.title = title;
        this.nickName = nickName;
        this.explain = explain;
        this.viewsCount = viewsCount;
        this.recipeImg = recipeImg;
        this.ingredients = ingredients;
        this.tags = tags;
    }

    public RecipeListItem(String title, String nickName, String explain, int viewsCount, String recipeImg) {
        this.title = title;
        this.nickName = nickName;
        this.explain = explain;
        this.viewsCount = viewsCount;
        this.recipeImg = recipeImg;
    }

    public ArrayList<String> getIngredients() {
        return ingredients;
    }

    public void setIngredients(ArrayList<String> ingredients) {
        this.ingredients = ingredients;
    }

    public ArrayList<String> getTags() {
        return tags;
    }

    public void setTags(ArrayList<String> tags) {
        this.tags = tags;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getNickName() {
        return nickName;
    }

    public void setNickName(String nickName) {
        this.nickName = nickName;
    }

    public String getExplain() {
        return explain;
    }

    public void setExplain(String explain) {
        this.explain = explain;
    }

    public int getViewsCount() {
        return viewsCount;
    }

    public void setViewsCount(int viewsCount) {
        this.viewsCount = viewsCount;
    }

    public String getRecipeImg() {
        return recipeImg;
    }

    public void setRecipeImg(String recipeImg) {
        this.recipeImg = recipeImg;
    }
}
