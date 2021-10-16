package com.yhjoo.dochef.data.dao

import androidx.room.*
import com.yhjoo.dochef.data.entity.NotificationEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface NotificationDao {
    @Query("SELECT * from notification_table WHERE date_time > :dateTime ORDER BY date_time DESC")
    fun getRecentList(dateTime: Long): Flow<List<NotificationEntity>>

    @Query("UPDATE notification_table SET is_read = 1 WHERE id = :id")
    fun setRead(id: Long) : Int

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(notification: NotificationEntity): Long

    @Update
    fun update(notification: NotificationEntity): Int

    @Delete
    fun delete(notification: NotificationEntity): Int
}