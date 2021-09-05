package com.yhjoo.dochef.data.model

import com.google.gson.annotations.SerializedName

class Review(
    @field:SerializedName("review_id") val reviewID: Int,
    @field:SerializedName(
        "recipe_id"
    ) val postID: Int,
    @field:SerializedName("user_id") val userID: String,
    @field:SerializedName(
        "nickname"
    ) val nickname: String,
    @field:SerializedName("user_profile_img") val userImg: String,
    @field:SerializedName(
        "contents"
    ) val contents: String,
    @field:SerializedName("rating") val rating: Long,
    @field:SerializedName(
        "datetime"
    ) val dateTime: Long
) {

    override fun toString(): String {
        return "Review{" +
                "reviewID=" + reviewID +
                ", postID=" + postID +
                ", userID='" + userID + '\'' +
                ", nickname='" + nickname + '\'' +
                ", userImg='" + userImg + '\'' +
                ", contents='" + contents + '\'' +
                ", rating=" + rating +
                ", dateTime=" + dateTime +
                '}'
    }
}