package com.yhjoo.dochef.db

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.yhjoo.dochef.db.dao.NotificationDao
import com.yhjoo.dochef.db.entity.NotificationEntity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch


@Database(entities = [NotificationEntity::class], version = 1)
abstract class NotificationDatabase : RoomDatabase() {
    abstract val notificationDao: NotificationDao

    companion object {
        @Volatile
        private var INSTANCE: NotificationDatabase? = null
        fun getInstance(context: Context): NotificationDatabase {
            synchronized(this) {
                var instance = INSTANCE
                if (instance == null) {
                    instance = Room.databaseBuilder(
                        context.applicationContext,
                        NotificationDatabase::class.java,
                        "subscriber_data_database"
                    ).build()
                }
                return instance
            }
        }
    }
}