package com.yhjoo.dochef.model

import com.google.gson.annotations.SerializedName
import java.io.Serializable
import java.util.*

data class Post(
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
) : Serializable