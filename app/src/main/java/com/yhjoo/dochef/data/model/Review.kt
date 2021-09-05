package com.yhjoo.dochef.data.model

import com.google.gson.annotations.SerializedName

class Review(
    @SerializedName("review_id") val reviewID: Int,
    @SerializedName("recipe_id") val postID: Int,
    @SerializedName("user_id") val userID: String,
    @SerializedName("nickname") val nickname: String,
    @SerializedName("user_profile_img") val userImg: String,
    @SerializedName("contents") val contents: String,
    @SerializedName("rating") val rating: Long,
    @SerializedName("datetime") val dateTime: Long
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