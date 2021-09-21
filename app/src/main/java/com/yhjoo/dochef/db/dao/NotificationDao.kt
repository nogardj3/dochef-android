package com.yhjoo.dochef.db.dao

import androidx.room.*
import com.yhjoo.dochef.db.entity.NotificationEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface NotificationDao {
    @Query("SELECT * FROM notification_table")
    fun getAllNotifications(): Flow<List<NotificationEntity>>

    @Query("SELECT * from notification_table WHERE date_time > :dateTime ORDER BY date_time DESC")
    fun getRecentList(dateTime: Long): Flow<List<NotificationEntity>>

    @Query("UPDATE notification_table SET is_read = 1 WHERE id = :id")
    fun setRead(id: Long)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(notification: NotificationEntity): Long

    @Update
    fun update(notification: NotificationEntity): Int

    @Delete
    fun delete(notification: NotificationEntity): Int
}