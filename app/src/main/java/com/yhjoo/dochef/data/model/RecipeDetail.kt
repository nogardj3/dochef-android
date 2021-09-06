package com.yhjoo.dochef.data.model

import com.google.gson.annotations.SerializedName
import java.io.Serializable
import java.util.*

data class RecipeDetail (
    @SerializedName("recipe_id") val recipeID: Int,
    @SerializedName("recipe_name") val recipeName: String,
    @SerializedName("user_id") val userID: String,
    @SerializedName("nickname") val nickname: String,
    @SerializedName("user_profile_img") val userImg: String,
    @SerializedName("recipe_img") val recipeImg: String,
    @SerializedName("contents") val contents: String,
    @SerializedName("datetime") val datetime: Long,
    @SerializedName("amount_time") val amountTime: String,
    @SerializedName("view_count") val viewCount: Int,
    @SerializedName("rating") val rating: Float,
    @SerializedName("ingredients") val ingredients: ArrayList<Ingredient>,
    @SerializedName("likes") val likes: ArrayList<String?>,
    @SerializedName("tags") val tags: ArrayList<String>,
    @SerializedName("phase") val phases: ArrayList<RecipePhase>
) : Serializable