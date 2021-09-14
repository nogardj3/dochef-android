package com.yhjoo.dochef.db.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import com.yhjoo.dochef.db.entity.NotificationEntity

@Dao
interface NotificationDao {
    @Insert
    fun insert(notification: NotificationEntity)

    @Query("UPDATE chef_notification SET is_read = 1 WHERE id = :id")
    fun setLike(id: Long)

    @Query("SELECT * from chef_notification WHERE date_time > :dateTime ORDER BY date_time DESC")
    fun getRecentList(dateTime: Long): List<NotificationEntity>

    @Delete
    fun delete(notification: NotificationEntity)
}