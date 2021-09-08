package com.yhjoo.dochef.utils

import android.content.Context
import androidx.room.*
import com.yhjoo.dochef.data.model.NotificationItem

@Dao
interface NotificationDao{
    @Insert
    fun insert(notification:NotificationItem)

    @Query("UPDATE chef_notification SET is_read = 1 WHERE id = :id")
    fun setLike(id: Long)

    @Query("SELECT * from chef_notification WHERE date_time > :dateTime ORDER BY date_time DESC")
    fun getRecentList(dateTime: Long) : List<NotificationItem>

    @Delete
    fun delete(notification:NotificationItem)
}

@Database(entities = [NotificationItem::class],version = 1)
abstract class NotificationDatabase: RoomDatabase() {
    abstract fun notificationDao(): NotificationDao

    companion object {
        private var instance: NotificationDatabase? = null

        @Synchronized
        fun getInstance(context: Context): NotificationDatabase? {
            if (instance == null) {
                synchronized(NotificationDatabase::class){
                    instance = Room.databaseBuilder(
                        context.applicationContext,
                        NotificationDatabase::class.java,
                        "chef_notification"
                    ).build()
                }
            }
            return instance
        }
    }
}