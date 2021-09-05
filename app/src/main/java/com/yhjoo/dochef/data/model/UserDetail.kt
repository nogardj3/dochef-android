package com.yhjoo.dochef.data.model

import com.google.gson.annotations.SerializedName
import java.util.*

class UserDetail(
    @field:SerializedName("user_id") val userID: String,
    @field:SerializedName(
        "user_profile_img"
    ) val userImg: String,
    @field:SerializedName("nickname") val nickname: String,
    @field:SerializedName(
        "bio"
    ) val profileText: String,
    @field:SerializedName("recipe_count") val recipeCount: Int,
    @field:SerializedName(
        "follow"
    ) val follow: ArrayList<String>,
    @field:SerializedName("follower_count") val followerCount: Int,
    @field:SerializedName(
        "following_count"
    ) val followingCount: Int
) {

    override fun toString(): String {
        return "UserDetail{" +
                "userID='" + userID + '\'' +
                ", userImg='" + userImg + '\'' +
                ", nickname='" + nickname + '\'' +
                ", profileText='" + profileText + '\'' +
                ", recipeCount=" + recipeCount +
                ", follow=" + follow +
                ", followerCount=" + followerCount +
                ", followingCount=" + followingCount +
                '}'
    }
}