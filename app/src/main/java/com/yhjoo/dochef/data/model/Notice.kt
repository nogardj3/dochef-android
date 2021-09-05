package com.yhjoo.dochef.data.model

import com.google.gson.annotations.SerializedName

class Notice(
    @SerializedName("title") val title: String, @SerializedName(
        "contents"
    ) val contents: String, @SerializedName("datetime") val dateTime: Long
) {
    override fun toString(): String {
        return "Notice{" +
                "title='" + title + '\'' +
                ", contents='" + contents + '\'' +
                ", dateTime=" + dateTime +
                '}'
    }
}