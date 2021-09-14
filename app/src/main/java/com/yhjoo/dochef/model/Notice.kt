package com.yhjoo.dochef.model

import com.google.gson.annotations.SerializedName

class Notice(
    @SerializedName("title") val title: String,
    @SerializedName("contents") val contents: String,
    @SerializedName("datetime") val dateTime: Long
)