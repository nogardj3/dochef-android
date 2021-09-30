package com.yhjoo.dochef.ui.account

import android.content.Intent
import android.os.Bundle
import android.view.*
import androidx.activity.viewModels
import androidx.core.content.edit
import androidx.core.os.bundleOf
import androidx.databinding.DataBindingUtil
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.tasks.Task
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.auth.*
import com.google.firebase.messaging.FirebaseMessaging
import com.google.gson.Gson
import com.yhjoo.dochef.App
import com.yhjoo.dochef.Constants
import com.yhjoo.dochef.R
import com.yhjoo.dochef.data.model.UserBrief
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

            accountViewModel.firebaseAnalytics.value =
                FirebaseAnalytics.getInstance(this@AccountActivity)
            accountViewModel.firebaseAuth.value = FirebaseAuth.getInstance()
            accountViewModel.googleClient.value = GoogleSignIn.getClient(
                this@AccountActivity,
                GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
//                .requestIdToken(getString(R.string.default_web_client_id))
                    .requestIdToken("227618773978-c5ptgsjltcrv8hl1dmgci6rnedd8ene9.apps.googleusercontent.com")
                    .requestEmail()
                    .build()
            )
            FirebaseMessaging.getInstance().token
                .addOnCompleteListener { task: Task<String?> ->
                    if (!task.isSuccessful) {
                        OtherUtil.log(task.exception.toString())
                        return@addOnCompleteListener
                    } else
                        accountViewModel.fcmToken.value = task.result!!
                }

            accountViewModel.phaseError.observe(this@AccountActivity, {
                OtherUtil.log(it.toString())
                progressOFF()
                App.showToast(it.second)
            })
            accountViewModel.phaseAllComplete.observe(this@AccountActivity, {
                startMain(it)
            })
        }
    }

    private fun startMain(userinfo: UserBrief) {
        OtherUtil.log(userinfo.toString())
        DatastoreUtil.getSharedPreferences(this).edit {
            putBoolean(getString(R.string.SP_ACTIVATEDDEVICE), true)
            putString(getString(R.string.SP_USERINFO), Gson().toJson(userinfo))
            apply()
        }

        val bundle = bundleOf(
            Pair(FirebaseAnalytics.Param.ITEM_ID, Constants.ANALYTICS.ID.SIGNIN),
            Pair(FirebaseAnalytics.Param.ITEM_NAME, Constants.ANALYTICS.NAME.SIGNIN)
        )

        accountViewModel.firebaseAnalytics.value!!.logEvent(FirebaseAnalytics.Event.LOGIN, bundle)

        progressOFF()
        startActivity(Intent(this, MainActivity::class.java))
        finish()
    }
}