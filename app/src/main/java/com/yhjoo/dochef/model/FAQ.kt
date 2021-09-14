package com.yhjoo.dochef.model

import com.google.gson.annotations.SerializedName

data class FAQ(
    @SerializedName("title") val title: String,
    @SerializedName("contents") val contents: String
)