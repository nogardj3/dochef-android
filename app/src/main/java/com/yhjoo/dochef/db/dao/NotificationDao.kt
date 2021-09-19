package com.yhjoo.dochef.db.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import com.yhjoo.dochef.db.entity.NotificationEntity

@Dao
interface NotificationDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(notification: NotificationEntity)

    @Query("SELECT * from notification_table WHERE date_time > :dateTime ORDER BY date_time DESC")
    fun getRecentList(dateTime: Long): LiveData<List<NotificationEntity>>

    @Query("UPDATE notification_table SET is_read = 1 WHERE id = :id")
    fun setRead(id: Long)

    @Update
    fun update(notification: NotificationEntity)

    @Delete
    fun delete(notification: NotificationEntity)
}