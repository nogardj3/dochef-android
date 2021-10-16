package com.yhjoo.dochef.ui.account

import android.content.Intent
import android.os.Bundle
import android.view.*
import androidx.activity.viewModels
import androidx.databinding.DataBindingUtil
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import com.google.firebase.auth.*
import com.yhjoo.dochef.R
import com.yhjoo.dochef.data.repository.AccountRepository
import com.yhjoo.dochef.databinding.AccountActivityBinding
import com.yhjoo.dochef.ui.base.BaseActivity
import com.yhjoo.dochef.ui.main.MainActivity
import com.yhjoo.dochef.utils.*
import kotlinx.coroutines.flow.collect

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
            viewModel = accountViewModel
        }

        eventOnLifecycle {
            accountViewModel.eventResult.collect {
                if (it.first == AccountViewModel.Events.Complete.COMPLETE) {
                    hideProgress()
                    startActivity(Intent(this@AccountActivity, MainActivity::class.java))
                    finish()
                }
            }
        }
    }
}