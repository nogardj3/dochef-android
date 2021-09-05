package com.yhjoo.dochef.services

import android.app.NotificationManager
import android.app.PendingIntent
import android.content.ContentValues
import android.content.Intent
import android.media.RingtoneManager
import android.preference.PreferenceManager
import androidx.core.app.NotificationCompat
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.yhjoo.dochef.R
import com.yhjoo.dochef.activities.NotificationActivity
import com.yhjoo.dochef.utils.ChefSQLite
import com.yhjoo.dochef.utils.ChefSQLite.NotificationEntry
import com.yhjoo.dochef.utils.Utils

class ChefMessagingService : FirebaseMessagingService() {
    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        Utils.log("Message : " + remoteMessage.data.toString())
        if (remoteMessage.notification != null) {
            Utils.log(
                "Message Notification Body: " + remoteMessage.notification!!
                    .body
            )
            val type = remoteMessage.data["type"]
            if (type != "0") addDB(remoteMessage.data)
            val mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(
                applicationContext
            )
            val settingEnable = mSharedPreferences.getBoolean(
                resources.getStringArray(R.array.sp_noti)[type!!.toInt()], true
            )
            if (settingEnable) sendNotification(
                remoteMessage.notification!!.title, remoteMessage.notification!!.body
            )
        }
    }

    private fun addDB(data: Map<String, String>) {
        val chefSQLite = ChefSQLite(
            this, ChefSQLite.Companion.DATABASE_NAME,
            null, ChefSQLite.Companion.DATABASE_VERSION
        )
        val db = chefSQLite.writableDatabase
        val values = ContentValues()
        values.put(NotificationEntry.COLUMN_NAME_TYPE, Integer.valueOf(data["type"]))
        values.put(NotificationEntry.COLUMN_NAME_INTENT, data["target_intent"])
        values.put(NotificationEntry.COLUMN_NAME_INTENT_DATA, data["target_intent_data"])
        values.put(NotificationEntry.COLUMN_NAME_CONTENTS, data["notification_contents"])
        values.put(NotificationEntry.COLUMN_NAME_IMG, data["notification_img"])
        values.put(
            NotificationEntry.COLUMN_NAME_DATETIME,
            java.lang.Long.valueOf(data["notification_datetime"])
        )
        values.put(NotificationEntry.COLUMN_NAME_READ, 0)
        val newRowId = db.insert(NotificationEntry.TABLE_NAME, null, values)
    }

    private fun sendNotification(title: String?, messageBody: String?) {
        val intent = Intent(this, NotificationActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT)
        val pendingIntent = PendingIntent.getActivity(
            this, 0, intent,
            PendingIntent.FLAG_ONE_SHOT
        )
        val defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
        val notificationBuilder = NotificationCompat.Builder(
            this,
            applicationContext.getString(R.string.notification_channel_id)
        )
            .setSmallIcon(R.drawable.logo)
            .setContentTitle(title)
            .setContentText(messageBody)
            .setAutoCancel(true)
            .setSound(defaultSoundUri)
            .setContentIntent(pendingIntent)
        val notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.notify(0, notificationBuilder.build())
    }

    override fun onNewToken(s: String) {
        super.onNewToken(s)
    }
}