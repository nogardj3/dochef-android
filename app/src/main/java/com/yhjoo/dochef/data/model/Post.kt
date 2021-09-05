package com.yhjoo.dochef.data.model

import com.google.gson.annotations.SerializedName
import java.io.Serializable
import java.util.*

class Post(
    @SerializedName("post_id") val postID: Int,
    @SerializedName("user_id") val userID: String,
    @SerializedName("nickname") val nickname: String,
    @SerializedName("user_profile_img") val userImg: String,
    @SerializedName("post_img") val postImg: String,
    @SerializedName("datetime") val dateTime: Long,
    @SerializedName("contents") val contents: String,
    @SerializedName("tags") val tags: ArrayList<String>,
    @SerializedName("comments") val comments: ArrayList<Comment?>,
    @SerializedName("likes") val likes: ArrayList<String?>
) : Serializable {
    override fun toString(): String {
        return "Post{" +
                "postID=" + postID +
                ", UserID='" + userID + '\'' +
                ", postImg='" + postImg + '\'' +
                ", contents='" + contents + '\'' +
                ", dateTime=" + dateTime +
                ", tags=" + tags +
                ", comments=" + comments +
                ", likes=" + likes +
                ", nickname='" + nickname + '\'' +
                ", userImg='" + userImg + '\'' +
                '}'
    }
}