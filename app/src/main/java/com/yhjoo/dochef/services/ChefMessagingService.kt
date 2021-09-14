package com.yhjoo.dochef.services

import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Intent
import android.media.RingtoneManager
import androidx.core.app.NotificationCompat
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.yhjoo.dochef.R
import com.yhjoo.dochef.db.NotificationDatabase
import com.yhjoo.dochef.db.entity.NotificationEntity
import com.yhjoo.dochef.ui.activities.NotificationActivity
import com.yhjoo.dochef.utilities.Utils
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class ChefMessagingService : FirebaseMessagingService() {
    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        Utils.log("Message : " + remoteMessage.data.toString())

        if (remoteMessage.notification != null) {
            Utils.log(
                "Message Notification Body: " + remoteMessage.notification!!.body
            )

            val type = remoteMessage.data["type"]
            if (type != "0") addDB(remoteMessage.data)

            val mSharedPreferences = Utils.getSharedPreferences(this)
            val settingEnable = mSharedPreferences.getBoolean(
                resources.getStringArray(R.array.sp_noti)[type!!.toInt()], true
            )

            if (settingEnable) sendNotification(
                remoteMessage.notification!!.title, remoteMessage.notification!!.body
            )
        }
    }

    private fun addDB(data: Map<String, String>) {
        val notiData = NotificationEntity(
            null,
            data["type"]!!.toInt(),
            data["target_intent"]!!,
            data["target_intent_data"]!!,
            data["notification_contents"]!!,
            data["notification_img"]!!,
            data["notification_datetime"]!!.toLong(),
            0
        )

        val db = NotificationDatabase.getInstance(applicationContext)
        CoroutineScope(Dispatchers.IO).launch {
            db!!.notificationDao().insert(notiData)
            Utils.log("message received",data.toString())
        }
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
}