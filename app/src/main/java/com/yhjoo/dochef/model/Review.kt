package com.yhjoo.dochef.model

import com.google.gson.annotations.SerializedName

data class Review(
    @SerializedName("review_id") val reviewID: Int,
    @SerializedName("recipe_id") val postID: Int,
    @SerializedName("user_id") val userID: String,
    @SerializedName("nickname") val nickname: String,
    @SerializedName("user_profile_img") val userImg: String,
    @SerializedName("contents") val contents: String,
    @SerializedName("rating") val rating: Long,
    @SerializedName("datetime") val dateTime: Long
)