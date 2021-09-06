package com.yhjoo.dochef.data.model

data class Notification(
    private val _id: Int,
    val type: Int,
    val intentName: String,
    val intentData: String,
    val contents: String,
    val image: String,
    val dateTime: Long,
    val read: Int
)