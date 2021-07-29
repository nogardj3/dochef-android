package com.yhjoo.dochef.services;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.media.RingtoneManager;
import android.net.Uri;
import android.preference.PreferenceManager;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.yhjoo.dochef.R;
import com.yhjoo.dochef.activities.NotificationActivity;
import com.yhjoo.dochef.activities.SplashActivity;
import com.yhjoo.dochef.utils.ChefSQLite;
import com.yhjoo.dochef.utils.Utils;

import java.util.Map;

public class ChefMessagingService extends FirebaseMessagingService {
    public void onMessageReceived(RemoteMessage remoteMessage) {
        Utils.log("Message : " + remoteMessage.getData().toString());

        if (remoteMessage.getNotification() != null) {
            Utils.log("Message Notification Body: " + remoteMessage.getNotification().getBody());

            String type = remoteMessage.getData().get("type");

            if (!type.equals("0"))
                addDB(remoteMessage.getData());

            SharedPreferences mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
            boolean settingEnable = mSharedPreferences.getBoolean(
                    getResources().getStringArray(R.array.sp_noti)[Integer.parseInt(type)], true);

            if (settingEnable)
                sendNotification(remoteMessage.getNotification().getTitle(), remoteMessage.getNotification().getBody());
        }
    }

    private void addDB(Map<String, String> data) {
        ChefSQLite chefSQLite = new ChefSQLite(this, ChefSQLite.DATABASE_NAME,
                null, ChefSQLite.DATABASE_VERSION);

        SQLiteDatabase db = chefSQLite.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(ChefSQLite.NotificationEntry.COLUMN_NAME_TYPE, Integer.valueOf(data.get("type")));
        values.put(ChefSQLite.NotificationEntry.COLUMN_NAME_INTENT, data.get("target_intent"));
        values.put(ChefSQLite.NotificationEntry.COLUMN_NAME_INTENT_DATA, data.get("target_intent_data"));
        values.put(ChefSQLite.NotificationEntry.COLUMN_NAME_CONTENTS, data.get("notification_contents"));
        values.put(ChefSQLite.NotificationEntry.COLUMN_NAME_IMG, data.get("notification_img"));
        values.put(ChefSQLite.NotificationEntry.COLUMN_NAME_DATETIME, Long.valueOf(data.get("notification_datetime")));
        values.put(ChefSQLite.NotificationEntry.COLUMN_NAME_READ, 0);

        long newRowId = db.insert(ChefSQLite.NotificationEntry.TABLE_NAME, null, values);
    }

    private void sendNotification(String title, String messageBody) {
        Intent intent = new Intent(this, NotificationActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent,
                PendingIntent.FLAG_ONE_SHOT);

        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder notificationBuilder =
                new NotificationCompat.Builder(this, getApplicationContext().getString(R.string.notification_channel_id))
                        .setSmallIcon(R.drawable.logo)
                        .setContentTitle(title)
                        .setContentText(messageBody)
                        .setAutoCancel(true)
                        .setSound(defaultSoundUri)
                        .setContentIntent(pendingIntent);

        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        notificationManager.notify(0, notificationBuilder.build());
    }

    @Override
    public void onNewToken(@NonNull String s) {
        super.onNewToken(s);
    }
}