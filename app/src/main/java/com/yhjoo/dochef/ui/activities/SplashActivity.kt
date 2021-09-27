package com.yhjoo.dochef.ui.activities

import android.content.Intent
import android.os.Bundle
import com.github.florent37.viewanimator.ViewAnimator
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.analytics.ktx.logEvent
import com.yhjoo.dochef.App
import com.yhjoo.dochef.R
import com.yhjoo.dochef.databinding.SplashActivityBinding
import com.yhjoo.dochef.utilities.ChefAuth
import com.yhjoo.dochef.utilities.RetrofitBuilder
import com.yhjoo.dochef.utilities.RetrofitServices.BasicService
import com.yhjoo.dochef.utilities.Utils
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class SplashActivity : BaseActivity() {
    private val binding: SplashActivityBinding by lazy {
        SplashActivityBinding.inflate(
            layoutInflater
        )
    }
    private lateinit var firebaseAnalytics: FirebaseAnalytics
    private var serverAlive = false
    private var isLogin = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        firebaseAnalytics = FirebaseAnalytics.getInstance(this).apply {
            logEvent(FirebaseAnalytics.Event.APP_OPEN) {
                param(FirebaseAnalytics.Param.ITEM_ID, getString(R.string.analytics_id_start))
                param(FirebaseAnalytics.Param.ITEM_NAME, getString(R.string.analytics_name_start))
                param(FirebaseAnalytics.Param.CONTENT_TYPE, getString(R.string.analytics_type_text))
            }
        }

        checkServerAlive()
        checkIsAutoLogin()
    }

    override fun onStart() {
        super.onStart()

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
        if (serverAlive) {
            if (isLogin)
                startMain()
            else
                startAccount()
        } else {
            App.showToast("서버가 동작하지 않습니다. 체험모드로 실행합니다.")
            startMain()
        }
        finish()
    }

    private fun checkServerAlive() =
        CoroutineScope(Dispatchers.Main).launch {
            val basicService = RetrofitBuilder.create(this@SplashActivity, BasicService::class.java)

            runCatching {
                basicService.checkAlive()
            }.onSuccess {
                App.isServerAlive = true
                serverAlive = true
            }.onFailure {
                App.isServerAlive = false
                serverAlive = false
                RetrofitBuilder.defaultErrorHandler(it)
            }
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

}