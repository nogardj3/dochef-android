package com.yhjoo.dochef.data.model

import com.google.gson.annotations.SerializedName

class Comment(
    @SerializedName("comment_id") val commentID: Int,
    @SerializedName("post_id") val postID: Int,
    @SerializedName("user_id") val userID: String,
    @SerializedName("nickname") val nickName: String,
    @SerializedName("user_profile_img") val userImg: String,
    @SerializedName("contents") val contents: String,
    @SerializedName("datetime") val dateTime: Long
) {
    override fun toString(): String {
        return "Comment{" +
                "commentID=" + commentID +
                ", postID=" + postID +
                ", userID='" + userID + '\'' +
                ", nickName='" + nickName + '\'' +
                ", userImg='" + userImg + '\'' +
                ", contents='" + contents + '\'' +
                ", dateTime=" + dateTime +
                '}'
    }
}