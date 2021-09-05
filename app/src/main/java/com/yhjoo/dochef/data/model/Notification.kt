package com.yhjoo.dochef.data.model

class Notification(
    private val _id: Int,
    val type: Int,
    val intent_name: String,
    val intent_data: String,
    val contents: String,
    val image: String,
    val dateTime: Long,
    val read: Int
) {
    override fun toString(): String {
        return "Notification{" +
                "_id=" + _id +
                ", type=" + type +
                ", intent_name='" + intent_name + '\'' +
                ", intent_data='" + intent_data + '\'' +
                ", contents='" + contents + '\'' +
                ", image='" + image + '\'' +
                ", dateTime=" + dateTime +
                ", read=" + read +
                '}'
    }
}