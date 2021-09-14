package com.yhjoo.dochef.db.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "chef_notification")
data class NotificationEntity(
    @PrimaryKey(autoGenerate = true) val id: Long? = null,
    @ColumnInfo(name = "type") val type: Int,
    @ColumnInfo(name = "intent") val intent: String,
    @ColumnInfo(name = "intent_data") val intentData: String,
    @ColumnInfo(name = "contents") val contents: String,
    @ColumnInfo(name = "img") val img: String,
    @ColumnInfo(name = "date_time") val dateTime: Long,
    @ColumnInfo(name = "is_read") val isRead: Int,
)