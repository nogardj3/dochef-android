package com.yhjoo.dochef.utils

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteDatabase.CursorFactory
import android.database.sqlite.SQLiteOpenHelper
import android.provider.BaseColumns

class ChefSQLite(context: Context?, name: String?, factory: CursorFactory?, version: Int) :
    SQLiteOpenHelper(context, name, factory, version) {
    object NotificationEntry : BaseColumns {
        const val TABLE_NAME = "notification"
        const val COLUMN_NAME_TYPE = "notificationtype"
        const val COLUMN_NAME_INTENT = "intent"
        const val COLUMN_NAME_INTENT_DATA = "intent_data"
        const val COLUMN_NAME_CONTENTS = "contents"
        const val COLUMN_NAME_IMG = "image"
        const val COLUMN_NAME_DATETIME = "datetime"
        const val COLUMN_NAME_READ = "isread"
    }

    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL(SQL_CREATE_ENTRIES)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL(SQL_DELETE_ENTRIES)
        onCreate(db)
    }

    override fun onDowngrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        onUpgrade(db, oldVersion, newVersion)
    }

    companion object {
        const val DATABASE_VERSION = 1
        const val DATABASE_NAME = "Chef.db"
        private const val SQL_CREATE_ENTRIES =
            "CREATE TABLE " + NotificationEntry.TABLE_NAME + " (" +
                    BaseColumns._ID + " INTEGER PRIMARY KEY," +
                    NotificationEntry.COLUMN_NAME_TYPE + " INTEGER," +
                    NotificationEntry.COLUMN_NAME_INTENT + " TEXT," +
                    NotificationEntry.COLUMN_NAME_INTENT_DATA + " TEXT," +
                    NotificationEntry.COLUMN_NAME_CONTENTS + " TEXT," +
                    NotificationEntry.COLUMN_NAME_IMG + " TEXT," +
                    NotificationEntry.COLUMN_NAME_DATETIME + " INTEGER," +
                    NotificationEntry.COLUMN_NAME_READ + " INTEGER)"
        private const val SQL_DELETE_ENTRIES =
            "DROP TABLE IF EXISTS " + NotificationEntry.TABLE_NAME
    }
}