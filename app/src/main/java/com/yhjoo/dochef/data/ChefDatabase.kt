package com.yhjoo.dochef.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.yhjoo.dochef.data.dao.NotificationDao
import com.yhjoo.dochef.data.entity.NotificationEntity

@Database(entities = [NotificationEntity::class], version = 1, exportSchema = false)
abstract class ChefDatabase : RoomDatabase() {
    abstract fun notificationDao(): NotificationDao

    companion object {
        @Volatile
        private var instance: ChefDatabase? = null

        fun getInstance(context: Context): ChefDatabase {
            return instance ?: synchronized(this) {
                instance ?: buildDatabase(context).also { instance = it }
            }
        }

        private fun buildDatabase(context: Context): ChefDatabase {
            return Room.databaseBuilder(
                context.applicationContext,
                ChefDatabase::class.java,
                "notification_table"
            )
                .build()
        }
    }
}