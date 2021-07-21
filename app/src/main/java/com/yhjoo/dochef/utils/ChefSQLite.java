package com.yhjoo.dochef.utils;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.BaseColumns;

import androidx.annotation.Nullable;

public class ChefSQLite extends SQLiteOpenHelper {
    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "Chef.db";

    public static class NotificationEntry implements BaseColumns {
        public static final String TABLE_NAME = "notification";
        public static final String COLUMN_NAME_TYPE = "notificationtype";
        public static final String COLUMN_NAME_INTENT = "intent";
        public static final String COLUMN_NAME_INTENT_DATA = "intent_data";
        public static final String COLUMN_NAME_CONTENTS = "contents";
        public static final String COLUMN_NAME_IMG = "image";
        public static final String COLUMN_NAME_DATETIME = "datetime";
        public static final String COLUMN_NAME_READ = "isread";
    }

    private static final String SQL_CREATE_ENTRIES =
            "CREATE TABLE " + NotificationEntry.TABLE_NAME + " (" +
                    NotificationEntry._ID + " INTEGER PRIMARY KEY," +
                    NotificationEntry.COLUMN_NAME_TYPE + " INTEGER," +
                    NotificationEntry.COLUMN_NAME_INTENT + " TEXT," +
                    NotificationEntry.COLUMN_NAME_INTENT_DATA + " TEXT," +
                    NotificationEntry.COLUMN_NAME_CONTENTS + " TEXT," +
                    NotificationEntry.COLUMN_NAME_IMG + " TEXT," +
                    NotificationEntry.COLUMN_NAME_DATETIME + " INTEGER," +
                    NotificationEntry.COLUMN_NAME_READ + " INTEGER)";

    private static final String SQL_DELETE_ENTRIES =
            "DROP TABLE IF EXISTS " + NotificationEntry.TABLE_NAME;

    public ChefSQLite(@Nullable Context context, @Nullable String name, @Nullable SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE_ENTRIES);
    }

    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(SQL_DELETE_ENTRIES);
        onCreate(db);
    }

    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        onUpgrade(db, oldVersion, newVersion);
    }
}
