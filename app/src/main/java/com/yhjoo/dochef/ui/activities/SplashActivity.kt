package com.yhjoo.dochef.activities

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Intent
import android.os.Build
import android.os.Bundle
import androidx.preference.PreferenceManager
import com.github.florent37.viewanimator.ViewAnimator
import com.google.android.gms.tasks.Task
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.messaging.FirebaseMessaging
import com.google.gson.JsonObject
import com.yhjoo.dochef.App.Companion.appInstance
import com.yhjoo.dochef.R
import com.yhjoo.dochef.databinding.ASplashBinding
import com.yhjoo.dochef.utils.RxRetrofitServices.BasicService
import com.yhjoo.dochef.utils.*
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import retrofit2.Response

class SplashActivity : BaseActivity() {
    lateinit var binding: ASplashBinding
    lateinit var mFirebaseAnalytics: FirebaseAnalytics
    var serverAlive = false
    var isLogin = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ASplashBinding.inflate(layoutInflater)
        setContentView(binding.root)
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this)
        checkServerAlive()
        checkIsAutoLogin()
        createChannel()
    }

    override fun onStart() {
        super.onStart()
        val bundle = Bundle()
        bundle.putString(FirebaseAnalytics.Param.ITEM_ID, getString(R.string.analytics_id_start))
        bundle.putString(
            FirebaseAnalytics.Param.ITEM_NAME,
            getString(R.string.analytics_name_start)
        )
        bundle.putString(
            FirebaseAnalytics.Param.CONTENT_TYPE,
            getString(R.string.analytics_type_text)
        )
        mFirebaseAnalytics!!.logEvent(FirebaseAnalytics.Event.APP_OPEN, bundle)
        ViewAnimator.animate(binding!!.splashLogo)
            .alpha(0.0f, 1.0f)
            .accelerate()
            .duration(500)
            .thenAnimate(binding!!.splashLogo)
            .alpha(1.0f, 1.0f)
            .duration(300)
            .onStop { goWhere() }
            .start()
    }

    fun goWhere() {
        Utils.log(serverAlive.toString() + "", isLogin.toString() + "")
        // 정상상태
        if (serverAlive && isLogin) startMain() else if (!isLogin) startAccount() else {
            appInstance!!.showToast("서버가 동작하지 않습니다. 체험모드로 실행합니다.")
            startMain()
        }
        finish()
    }

    fun checkServerAlive() {
        val basicService = RxRetrofitBuilder.create(this, BasicService::class.java)
        compositeDisposable!!.add(
            basicService!!.checkAlive()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ response: Response<JsonObject?>? ->
                    appInstance.isServerAlive = true
                    serverAlive = true
                }) { throwable: Throwable ->
                    throwable.printStackTrace()
                    appInstance.isServerAlive = false
                    serverAlive = false
                }
        )
    }

    private fun checkIsAutoLogin() {
        isLogin = ChefAuth.isLogIn(this)
    }

    private fun startAccount() {
        startActivity(Intent(this, AccountActivity::class.java))
    }

    private fun startMain() {
        startActivity(Intent(this, MainActivity::class.java))
    }

    private fun createChannel() {
        val mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(applicationContext)
        val channelCreated = mSharedPreferences.getBoolean("channel_created", false)
        Utils.log(channelCreated)

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
                        Utils.log(task.exception.toString())
                    }
                    Utils.log(task.isSuccessful)
                }
            val editor = mSharedPreferences.edit()
            editor.putBoolean("channel_created", true)
            editor.apply()
        }
    }
}