package com.yhjoo.dochef.data.model

import com.google.gson.annotations.SerializedName

class FAQ(
    @SerializedName("title") val title: String, @SerializedName(
        "contents"
    ) val contents: String
) {

    override fun toString(): String {
        return "FAQ{" +
                "title='" + title + '\'' +
                ", contents='" + contents + '\'' +
                '}'
    }
}