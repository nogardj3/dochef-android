package com.yhjoo.dochef

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.os.Build
import android.widget.Toast
import androidx.core.content.edit
import com.google.android.gms.tasks.Task
import com.google.firebase.messaging.FirebaseMessaging
import com.orhanobut.logger.AndroidLogAdapter
import com.orhanobut.logger.Logger
import com.yhjoo.dochef.utils.DatastoreUtil
import com.yhjoo.dochef.utils.OtherUtil
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class App : Application() {
    companion object {
        var isServerAlive = false
        lateinit var activeUserId: String
        lateinit var toast: Toast

        fun showToast(text: String) {
            toast.setText(text)
            toast.show()
        }
    }

    override fun onCreate() {
        super.onCreate()

        Logger.addLogAdapter(AndroidLogAdapter())
        toast = Toast.makeText(this, "Default", Toast.LENGTH_SHORT)

        createNotificationChannel()

//-------- Rxjava 에러
//        RxJavaPlugins.setErrorHandler { e: Throwable ->
//            if (e is UndeliverableException) {
//                e = e.cause!!
//            }
//            if (e is IOException || e is SocketException) {
//                // fine, irrelevant network problem or API that throws on cancellation
//                return@setErrorHandler
//            }
//            if (e is InterruptedException) {
//                // fine, some blocking code was interrupted by a dispose call
//                return@setErrorHandler
//            }
//            if (e is NullPointerException || e is IllegalArgumentException) {
//                // that's likely a bug in the application Thread.currentThread().getUncaughtExceptionHandler() .uncaughtException(Thread.currentThread(), e);
//                return@setErrorHandler
//            }
//            if (e is IllegalStateException) { q// that's a bug in RxJava or in a custom operator
//                Thread.currentThread().uncaughtExceptionHandler.uncaughtException(
//                    Thread.currentThread(),
//                    e
//                )
//                return@setErrorHandler
//            }
//            Log.e(
//                "RxJava_HOOK",
//                "Undeliverable exception received, not sure what to do" + e.message
//            )
//        }

//-------- 세로 막기
//        registerActivityLifecycleCallbacks(object : ActivityLifecycleCallbacks {
//            override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) {
////                                                   activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
//            }
//            override fun onActivityStarted(activity: Activity) {}
//            override fun onActivityResumed(activity: Activity) {}
//            override fun onActivityPaused(activity: Activity) {}
//            override fun onActivityStopped(activity: Activity) {}
//            override fun onActivitySaveInstanceState(activity: Activity, outState: Bundle) {}
//            override fun onActivityDestroyed(activity: Activity) {}
//        }
//        )
    }


    private fun createNotificationChannel() {
        val mSharedPreferences = DatastoreUtil.getSharedPreferences(this)
        val channelCreated = mSharedPreferences.getBoolean("channel_created", false)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O && !channelCreated) {
            val channelID = getString(R.string.notification_channel_id)
            val name = "기본채널"
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val mChannel = NotificationChannel(channelID, name, importance)
            val notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(mChannel)

            FirebaseMessaging.getInstance().subscribeToTopic("admin")
                .addOnCompleteListener { task: Task<Void?> ->
                    if (!task.isSuccessful) {
                        task.exception!!.printStackTrace()
                        OtherUtil.log(task.exception.toString())
                    }
                }

            mSharedPreferences.edit {
                putBoolean("channel_created", true)
                apply()
            }
        }
    }
}