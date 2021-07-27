package com.yhjoo.dochef.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;

public class RecipePhase implements Serializable {
    @SerializedName("recipe_img")
    private String recipe_img;
    @SerializedName("contents")
    private String contents;
    @SerializedName("tips")
    private ArrayList<String> tips;
    @SerializedName("time_amount")
    private String time_amount;
    @SerializedName("ingredients")
    private ArrayList<Ingredient> ingredients;

    public RecipePhase(String recipe_img, String contents, ArrayList<String> tips, String time_amount, ArrayList<Ingredient> ingredients) {
        this.recipe_img = recipe_img;
        this.contents = contents;
        this.tips = tips;
        this.time_amount = time_amount;
        this.ingredients = ingredients;
    }

    public String getRecipe_img() {
        return recipe_img;
    }

    public String getContents() {
        return contents;
    }

    public ArrayList<String> getTips() {
        return tips;
    }

    public String getTime_amount() {
        return time_amount;
    }

    public ArrayList<Ingredient> getIngredients() {
        return ingredients;
    }

    @Override
    public String toString() {
        return "RecipePhase{" +
                "recipe_img='" + recipe_img + '\'' +
                ", contents='" + contents + '\'' +
                ", tips=" + tips +
                ", time_amount='" + time_amount + '\'' +
                ", ingredients=" + ingredients +
                '}';
    }
}
