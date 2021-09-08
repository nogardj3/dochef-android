package com.yhjoo.dochef.ui.activities

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Intent
import android.os.Build
import android.os.Bundle
import androidx.core.content.edit
import com.github.florent37.viewanimator.ViewAnimator
import com.google.android.gms.tasks.Task
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.analytics.ktx.logEvent
import com.google.firebase.messaging.FirebaseMessaging
import com.yhjoo.dochef.App
import com.yhjoo.dochef.R
import com.yhjoo.dochef.databinding.ASplashBinding
import com.yhjoo.dochef.utils.ChefAuth
import com.yhjoo.dochef.utils.RetrofitBuilder
import com.yhjoo.dochef.utils.RetrofitServices.BasicService
import com.yhjoo.dochef.utils.Utils
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.HttpException

class SplashActivity : BaseActivity() {
    private val binding: ASplashBinding by lazy { ASplashBinding.inflate(layoutInflater) }
    private lateinit var firebaseAnalytics: FirebaseAnalytics
    private var serverAlive = false
    private var isLogin = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        firebaseAnalytics = FirebaseAnalytics.getInstance(this)
        checkServerAlive()
        checkIsAutoLogin()
        createChannel()
    }

    override fun onStart() {
        super.onStart()
        firebaseAnalytics.logEvent(FirebaseAnalytics.Event.APP_OPEN) {
            param(FirebaseAnalytics.Param.ITEM_ID, getString(R.string.analytics_id_start))
            param(FirebaseAnalytics.Param.ITEM_NAME, getString(R.string.analytics_name_start))
            param(FirebaseAnalytics.Param.CONTENT_TYPE, getString(R.string.analytics_type_text))
        }

        ViewAnimator.animate(binding.splashLogo)
            .alpha(0.0f, 1.0f)
            .accelerate()
            .duration(500)
            .thenAnimate(binding.splashLogo)
            .alpha(1.0f, 1.0f)
            .duration(300)
            .onStop { goWhere() }
            .start()
    }

    private fun goWhere() {
        Utils.log(serverAlive.toString() + "", isLogin.toString() + "")
        if (serverAlive && isLogin) startMain() else if (!isLogin) startAccount() else {
            App.showToast("서버가 동작하지 않습니다. 체험모드로 실행합니다.")
            startMain()
        }
        finish()
    }

    private fun checkServerAlive() = CoroutineScope(Dispatchers.IO).launch {
        val basicService = RetrofitBuilder.create(this@SplashActivity, BasicService::class.java)

        basicService.checkAlive().runCatching {
            basicService.checkAlive()
        }.onSuccess {
            App.isServerAlive = true
            serverAlive = true
        }.onFailure {
            App.isServerAlive = false
            serverAlive = false

            RetrofitBuilder.defaultErrorHandler(it)
        }

        /*
        * RXJAVA
        val basicService = RxRetrofitBuilder.create(this, BasicService::class.java)
        compositeDisposable.add(
            basicService.checkAlive()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    App.isServerAlive = true
                    serverAlive = true
                }) { throwable: Throwable ->
                    throwable.printStackTrace()
                    App.isServerAlive = false
                    serverAlive = false
                }
        )
         */
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
        val mSharedPreferences = Utils.getSharedPreferences(this)
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
                        Utils.log(task.exception.toString())
                    }
                }

            mSharedPreferences.edit {
                putBoolean("channel_created", true)
                apply()
            }
        }
    }
}