package com.yhjoo.dochef

import android.app.Application
import android.widget.Toast
import com.orhanobut.logger.AndroidLogAdapter
import com.orhanobut.logger.Logger

class App : Application() {
    // TODO
    // 1. RX error Handler
    // 2. 세로 막기

    private lateinit var toast: Toast
    var isServerAlive = false

    companion object {
        lateinit var appInstance: App
    }

    override fun onCreate() {
        super.onCreate()

        Logger.addLogAdapter(AndroidLogAdapter())
        appInstance = this
        toast = Toast.makeText(this, "Default", Toast.LENGTH_SHORT)

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

    fun showToast(text: String) {
        toast.setText(text)
        toast.show()
    }
}