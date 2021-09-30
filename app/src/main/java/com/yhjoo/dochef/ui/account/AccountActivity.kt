package com.yhjoo.dochef.ui.account

import android.content.Intent
import android.os.Bundle
import android.view.*
import androidx.activity.viewModels
import androidx.core.os.bundleOf
import androidx.databinding.DataBindingUtil
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.auth.*
import com.yhjoo.dochef.Constants
import com.yhjoo.dochef.R
import com.yhjoo.dochef.data.repository.AccountRepository
import com.yhjoo.dochef.databinding.AccountActivityBinding
import com.yhjoo.dochef.ui.base.BaseActivity
import com.yhjoo.dochef.ui.main.MainActivity
import com.yhjoo.dochef.utils.*

class AccountActivity : BaseActivity() {
    private val binding: AccountActivityBinding by lazy {
        DataBindingUtil.setContentView(this, R.layout.account_activity)
    }
    private val accountViewModel: AccountViewModel by viewModels {
        AccountViewModelFactory(
            application,
            AccountRepository(applicationContext)
        )
    }

    private lateinit var navController: NavController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.account_host_fragment) as NavHostFragment
        navController = navHostFragment.navController

        binding.apply {
            lifecycleOwner = this@AccountActivity

            accountViewModel.phaseError.observe(this@AccountActivity, {
                OtherUtil.log(it.toString())
                hideProgress()
                showSnackBar(binding.root,it.second)
            })
            accountViewModel.phaseFindPWComplete.observe(this@AccountActivity, {
                if (it)
                    startMain()
            })
            accountViewModel.phaseAllComplete.observe(this@AccountActivity, {
                hideProgress()
                showSnackBar(binding.root,"메일을 전송했습니다. 메일을 확인 해 주세요.")
            })
        }
    }

    private fun startMain() {
        val bundle = bundleOf(
            Pair(FirebaseAnalytics.Param.ITEM_ID, Constants.ANALYTICS.ID.SIGNIN),
            Pair(FirebaseAnalytics.Param.ITEM_NAME, Constants.ANALYTICS.NAME.SIGNIN)
        )

        accountViewModel.analyticsLogEvent(FirebaseAnalytics.Event.LOGIN, bundle)

        hideProgress()
        startActivity(Intent(this, MainActivity::class.java))
        finish()
    }
}