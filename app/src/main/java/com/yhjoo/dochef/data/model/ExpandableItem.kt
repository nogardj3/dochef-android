package com.yhjoo.dochef.data.model

import com.google.gson.annotations.SerializedName

class ExpandableItem(
    @SerializedName("title") val title: String,
    @SerializedName("contents") val contents: String,
    @SerializedName("datetime") val dateTime: Long,
    var expanded: Boolean = false

) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as ExpandableItem

        if (title != other.title) return false
        if (contents != other.contents) return false
        if (dateTime != other.dateTime) return false
        if (expanded != other.expanded) return false

        return true
    }
}
