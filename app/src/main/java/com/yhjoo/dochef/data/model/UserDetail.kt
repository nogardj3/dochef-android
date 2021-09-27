package com.yhjoo.dochef.data.model

import com.google.gson.annotations.SerializedName
import java.util.*

data class UserDetail(
    @SerializedName("user_id") val userID: String,
    @SerializedName("user_profile_img") val userImg: String,
    @SerializedName("nickname") val nickname: String,
    @SerializedName("bio") val profileText: String,
    @SerializedName("recipe_count") val recipeCount: Int,
    @SerializedName("follow") val follow: ArrayList<String>,
    @SerializedName("follower_count") val followerCount: Int,
    @SerializedName("following_count") val followingCount: Int
)