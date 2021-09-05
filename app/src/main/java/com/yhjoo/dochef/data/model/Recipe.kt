package com.yhjoo.dochef.data.model

import com.google.gson.annotations.SerializedName
import java.util.*

class Recipe(
    @SerializedName("recipe_id") val recipeID: Int,
    @SerializedName("recipe_name") val recipeName: String,
    @SerializedName("user_id") val userID: String,
    @SerializedName("nickname") val nickname: String,
    @SerializedName("user_profile_img") val userImg: String,
    @SerializedName("recipe_img") val recipeImg: String,
    @SerializedName("contents") val contents: String,
    @SerializedName("datetime") val datetime: Long,
    @SerializedName("amount_time") val amount_time: String,
    @SerializedName("view_count") val view_count: Int,
    @SerializedName("rating") val rating: Float,
    @SerializedName("ingredients") val ingredient: ArrayList<Ingredient>,
    @SerializedName("tags") val tags: ArrayList<String>
) {
    override fun toString(): String {
        return "Recipe{" +
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
                ", rating=" + rating +
                ", ingredient=" + ingredient +
                ", tags=" + tags +
                '}'
    }
}