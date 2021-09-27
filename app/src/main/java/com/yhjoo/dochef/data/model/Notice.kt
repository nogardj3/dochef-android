package com.yhjoo.dochef.data.model

import com.google.gson.annotations.SerializedName

class Notice(
    @SerializedName("title") val title: String,
    @SerializedName("contents") val contents: String,
    var expanded: Boolean = false
)