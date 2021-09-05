package com.yhjoo.dochef.data.model

import com.google.gson.annotations.SerializedName
import java.io.Serializable
import java.util.*

class RecipeDetail(
    @field:SerializedName("recipe_id") val recipeID: Int,
    @field:SerializedName(
        "recipe_name"
    ) val recipeName: String,
    @field:SerializedName("user_id") val userID: String,
    @field:SerializedName(
        "nickname"
    ) val nickname: String,
    @field:SerializedName("user_profile_img") val userImg: String,
    @field:SerializedName(
        "recipe_img"
    ) val recipeImg: String,
    @field:SerializedName("contents") val contents: String,
    @field:SerializedName(
        "datetime"
    ) val datetime: Long,
    @field:SerializedName("amount_time") val amount_time: String,
    @field:SerializedName(
        "view_count"
    ) val view_count: Int,
    @field:SerializedName("likes") val likes: ArrayList<String?>,
    @field:SerializedName(
        "rating"
    ) val rating: Float,
    @field:SerializedName("ingredients") val ingredients: ArrayList<Ingredient>,
    @field:SerializedName(
        "tags"
    ) val tags: ArrayList<String>,
    @field:SerializedName("phase") val phases: ArrayList<RecipePhase>
) : Serializable {

    override fun toString(): String {
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
                '}'
    }
}