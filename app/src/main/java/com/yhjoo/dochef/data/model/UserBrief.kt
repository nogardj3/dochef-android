package com.yhjoo.dochef.data.model

import com.google.gson.annotations.SerializedName
import java.util.*

class UserBrief(
    @field:SerializedName("user_id") val userID: String,
    @field:SerializedName(
        "user_profile_img"
    ) val userImg: String,
    @field:SerializedName("nickname") val nickname: String,
    @field:SerializedName(
        "follow"
    ) val follow: ArrayList<String>,
    @field:SerializedName("follower_count") val follower_count: Int
) {

    override fun toString(): String {
        return "UserBrief{" +
                "userID='" + userID + '\'' +
                ", userImg='" + userImg + '\'' +
                ", nickname='" + nickname + '\'' +
                ", follow=" + follow +
                ", follower_count=" + follower_count +
                '}'
    }
}