package com.yhjoo.dochef.db

import android.content.Context
import androidx.room.*
import com.yhjoo.dochef.db.entity.NotificationEntity
import com.yhjoo.dochef.db.dao.NotificationDao


@Database(entities = [NotificationEntity::class],version = 1)
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