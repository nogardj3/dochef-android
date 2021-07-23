package com.yhjoo.dochef.activities;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.ViewGroup;

import androidx.recyclerview.widget.LinearLayoutManager;

import com.yhjoo.dochef.App;
import com.yhjoo.dochef.R;
import com.yhjoo.dochef.adapter.NotificationListAdapter;
import com.yhjoo.dochef.databinding.ANotificationBinding;
import com.yhjoo.dochef.model.Notification;
import com.yhjoo.dochef.utils.ChefSQLite;
import com.yhjoo.dochef.utils.DataGenerator;
import com.yhjoo.dochef.utils.Utils;

import java.util.ArrayList;

public class NotificationActivity extends BaseActivity {
    ANotificationBinding binding;
    ChefSQLite chefSQLite;
    NotificationListAdapter notificationListAdapter;

    ArrayList<Notification> notifications = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ANotificationBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setSupportActionBar(binding.notificationToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        chefSQLite = new ChefSQLite(this, ChefSQLite.DATABASE_NAME,
                null, ChefSQLite.DATABASE_VERSION);

        notificationListAdapter = new NotificationListAdapter();
        notificationListAdapter.setEmptyView(R.layout.rv_loading, (ViewGroup) binding.notificationRecycler.getParent());
        notificationListAdapter.setOnItemClickListener((adapter, view, position) -> {
            if (App.isServerAlive()) {
                SQLiteDatabase db2 = chefSQLite.getWritableDatabase();

                ContentValues values = new ContentValues();
                values.put(ChefSQLite.NotificationEntry.COLUMN_NAME_READ, 1);

                String selection = ChefSQLite.NotificationEntry._ID + " LIKE ?";
                String[] selectionArgs = {Integer.toString(notifications.get(position).get_id())};

                int count = db2.update(
                        ChefSQLite.NotificationEntry.TABLE_NAME,
                        values,
                        selection,
                        selectionArgs);

                int noti_type = notifications.get(position).getType();

                Utils.log(notifications.get(position).getType());
                if (noti_type == getResources().getInteger(R.integer.NOTIFICATION_TYPE_1)
                        || noti_type == getResources().getInteger(R.integer.NOTIFICATION_TYPE_2)) {
                    Intent intent = new Intent(NotificationActivity.this, RecipeDetailActivity.class)
                            .putExtra("recipeID", Integer.parseInt(notifications.get(position).getIntent_data()));
                    startActivity(intent);
                } else if (noti_type == getResources().getInteger(R.integer.NOTIFICATION_TYPE_3)) {
                    Intent intent = new Intent(NotificationActivity.this, PostDetailActivity.class)
                            .putExtra("postID", Integer.parseInt(notifications.get(position).getIntent_data()));
                    startActivity(intent);
                } else if (noti_type == getResources().getInteger(R.integer.NOTIFICATION_TYPE_4)) {
                    Intent intent = new Intent(NotificationActivity.this, HomeActivity.class)
                            .putExtra("postID", notifications.get(position).getIntent_data());
                    startActivity(intent);
                }

            }
        });
        binding.notificationRecycler.setLayoutManager(new LinearLayoutManager(this));
        binding.notificationRecycler.setAdapter(notificationListAdapter);
        notificationListAdapter.setNewData(notifications);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (App.isServerAlive()) {
            notifications = readDataFromDB();

        } else {
            notifications = DataGenerator.make(getResources(),
                    getResources().getInteger(R.integer.DATA_TYPE_NOTIFICATION));

        }
        notificationListAdapter.setNewData(notifications);
        notificationListAdapter.setEmptyView(R.layout.rv_empty, (ViewGroup) binding.notificationRecycler.getParent());
    }

    ArrayList<Notification> readDataFromDB() {
        ArrayList<Notification> res = new ArrayList<>();

        SQLiteDatabase db = chefSQLite.getReadableDatabase();

        String selection = ChefSQLite.NotificationEntry.COLUMN_NAME_DATETIME + " > ?";
        String[] selectionArgs = {Long.toString(System.currentTimeMillis() - 1000 * 60 * 60 * 24 * 3)};

        String sortOrder =
                ChefSQLite.NotificationEntry.COLUMN_NAME_DATETIME + " DESC";

        Cursor cursor = db.query(
                ChefSQLite.NotificationEntry.TABLE_NAME,   // The table to query
                null,             // The array of columns to return (pass null to get all)
                selection,              // The columns for the WHERE clause
                selectionArgs,          // The values for the WHERE clause
                null,                   // don't group the rows
                null,                   // don't filter by row groups
                sortOrder               // The sort order
        );


        while (cursor.moveToNext()) {
            res.add(new Notification(
                    cursor.getInt(
                            cursor.getColumnIndexOrThrow(ChefSQLite.NotificationEntry._ID)),
                    cursor.getInt(
                            cursor.getColumnIndexOrThrow(ChefSQLite.NotificationEntry.COLUMN_NAME_TYPE)),
                    cursor.getString(
                            cursor.getColumnIndexOrThrow(ChefSQLite.NotificationEntry.COLUMN_NAME_INTENT)),
                    cursor.getString(
                            cursor.getColumnIndexOrThrow(ChefSQLite.NotificationEntry.COLUMN_NAME_INTENT_DATA)),
                    cursor.getString(
                            cursor.getColumnIndexOrThrow(ChefSQLite.NotificationEntry.COLUMN_NAME_CONTENTS)),
                    cursor.getString(
                            cursor.getColumnIndexOrThrow(ChefSQLite.NotificationEntry.COLUMN_NAME_IMG)),
                    cursor.getLong(
                            cursor.getColumnIndexOrThrow(ChefSQLite.NotificationEntry.COLUMN_NAME_DATETIME)),
                    cursor.getInt(
                            cursor.getColumnIndexOrThrow(ChefSQLite.NotificationEntry.COLUMN_NAME_READ))
            ));
        }
        cursor.close();

        for (Notification noti : res) {
            Utils.log(noti.toString());
        }

        return res;
    }
}