package com.yhjoo.dochef.data.model

import com.google.gson.annotations.SerializedName

data class ExpandableItem(
    @SerializedName("title") val title: String,
    @SerializedName("contents") val contents: String,
    @SerializedName("datetime") val dateTime: Long,
    var expanded: Boolean = false
)