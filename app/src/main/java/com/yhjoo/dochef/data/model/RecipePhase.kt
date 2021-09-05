package com.yhjoo.dochef.data.model

import com.google.gson.annotations.SerializedName
import java.io.Serializable
import java.util.*

class RecipePhase(
    @SerializedName("recipe_img") val recipe_img: String,
    @SerializedName("contents") val contents: String,
    @SerializedName("tips") val tips: ArrayList<String>,
    @SerializedName("time_amount") val time_amount: String,
    @SerializedName("ingredients") val ingredients: ArrayList<Ingredient>
) : Serializable {
    override fun toString(): String {
        return "RecipePhase{" +
                "recipe_img='" + recipe_img + '\'' +
                ", contents='" + contents + '\'' +
                ", tips=" + tips +
                ", time_amount='" + time_amount + '\'' +
                ", ingredients=" + ingredients +
                '}'
    }
}