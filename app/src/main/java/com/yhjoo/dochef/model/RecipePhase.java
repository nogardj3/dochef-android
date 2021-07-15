package com.yhjoo.dochef.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;

public class RecipePhase implements Serializable {
    @SerializedName("phase_type")
    private int phase_type;
    @SerializedName("recipe_img")
    private String recipe_img;
    @SerializedName("contents")
    private String contents;
    @SerializedName("time_amount")
    private String time_amount;
    @SerializedName("ingredients")
    private ArrayList<Ingredient> ingredients;
    @SerializedName("tips")
    private ArrayList<String> tips;

    public RecipePhase(int phase_type, String recipe_img, String contents, String time_amount, ArrayList<Ingredient> ingredients, ArrayList<String> tips) {
        this.phase_type = phase_type;
        this.recipe_img = recipe_img;
        this.contents = contents;
        this.time_amount = time_amount;
        this.ingredients = ingredients;
        this.tips = tips;
    }

    public RecipePhase() {
    }

    public int getPhase_type() {
        return phase_type;
    }

    public String getRecipe_img() {
        return recipe_img;
    }

    public String getContents() {
        return contents;
    }

    public String getTime_amount() {
        return time_amount;
    }

    public ArrayList<Ingredient> getIngredients() {
        return ingredients;
    }

    public ArrayList<String> getTips() {
        return tips;
    }

    @Override
    public String toString() {
        return "RecipePhase{" +
                "phase_type=" + phase_type +
                ", recipe_img='" + recipe_img + '\'' +
                ", contents='" + contents + '\'' +
                ", time_amount='" + time_amount + '\'' +
                ", ingredients=" + ingredients +
                ", tips=" + tips +
                '}';
    }
}
