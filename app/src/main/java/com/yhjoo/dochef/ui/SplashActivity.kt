package com.yhjoo.dochef.ui

import android.content.Intent
import android.os.Bundle
import android.view.animation.AccelerateInterpolator
import androidx.activity.viewModels
import androidx.databinding.DataBindingUtil
import com.yhjoo.dochef.App
import com.yhjoo.dochef.R
import com.yhjoo.dochef.data.repository.BasicRepository
import com.yhjoo.dochef.databinding.SplashActivityBinding
import com.yhjoo.dochef.ui.account.AccountActivity
import com.yhjoo.dochef.ui.base.BaseActivity
import com.yhjoo.dochef.ui.main.MainActivity
import kotlinx.coroutines.flow.collect

class SplashActivity : BaseActivity() {
    private val binding: SplashActivityBinding by lazy {
        DataBindingUtil.setContentView(this, R.layout.splash_activity)
    }
    private val splashViewModel: SplashViewModel by viewModels {
        SplashViewModelFactory(
            application,
            BasicRepository(applicationContext)
        )
    }

    private var animFinished: Boolean = false
    private var lastEvent: SplashViewModel.Events? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        subscribeEventOnLifecycle {
            splashViewModel.eventResult.collect {
                lastEvent = it
                goWhere()
            }
        }
    }

    override fun onStart() {
        super.onStart()

        binding.splashLogo.animate()
            .alpha(1.0f)
            .setInterpolator(AccelerateInterpolator())
            .setStartDelay(500)
            .setDuration(300)
            .withEndAction {
                animFinished = true
                goWhere()
            }
            .start()
    }

    private fun goWhere() {
        if (animFinished && lastEvent != null) {
            when (lastEvent) {
                SplashViewModel.Events.ALIVE_WITH_LOGIN ->
                    startActivity(Intent(this, MainActivity::class.java))
                SplashViewModel.Events.ALIVE ->
                    startActivity(Intent(this, AccountActivity::class.java))
                SplashViewModel.Events.DEAD -> {
                    App.showToast("서버가 동작하지 않습니다. 체험모드로 실행합니다.")
                    startActivity(Intent(this, MainActivity::class.java))
                }
            }
            finish()
        }
    }
}