package com.yhjoo.dochef.ui.account

import android.content.Intent
import android.os.Bundle
import android.view.*
import androidx.core.content.edit
import androidx.core.os.bundleOf
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.tasks.Task
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.auth.*
import com.google.firebase.messaging.FirebaseMessaging
import com.google.gson.Gson
import com.yhjoo.dochef.App
import com.yhjoo.dochef.R
import com.yhjoo.dochef.data.model.UserBrief
import com.yhjoo.dochef.data.network.RetrofitBuilder
import com.yhjoo.dochef.data.network.RetrofitServices.AccountService
import com.yhjoo.dochef.databinding.AccountActivityBinding
import com.yhjoo.dochef.ui.base.BaseActivity
import com.yhjoo.dochef.ui.main.MainActivity
import com.yhjoo.dochef.utils.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class AccountActivity : BaseActivity() {
    val binding: AccountActivityBinding by lazy { AccountActivityBinding.inflate(layoutInflater) }

    private lateinit var googleSignInClient: GoogleSignInClient
    private lateinit var firebaseAnalytics: FirebaseAnalytics
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var accountService: AccountService
    private lateinit var fcmToken: String
    private lateinit var navController: NavController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        firebaseAnalytics = FirebaseAnalytics.getInstance(this)
        firebaseAuth = FirebaseAuth.getInstance()
        googleSignInClient = GoogleSignIn.getClient(
            this,
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
                }

                val token = task.result
                OtherUtil.log(token.toString())
                fcmToken = token!!
            }

        accountService = RetrofitBuilder.create(this, AccountService::class.java)

        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.account_host_fragment) as NavHostFragment
        navController = navHostFragment.navController
    }

    fun checkUserInfo(idToken: String, action: Int) {
        CoroutineScope(Dispatchers.Main).launch {
            runCatching {
                accountService.checkUser(idToken, firebaseAuth.uid!!, fcmToken)
            }.onSuccess {
                if (it.code() == 409) {
                    App.showToast("닉네임을 입력해주세요.")
                    val bundle = bundleOf("token" to idToken)
                    navController.navigate(action, bundle)
                    progressOFF()
                } else startMain(it.body()!!)
            }.onFailure {
                progressOFF()
                RetrofitBuilder.defaultErrorHandler(it)
            }
        }
    }

    fun startMain(userinfo: UserBrief) {
        OtherUtil.log(userinfo.toString())
        DatastoreUtil.getSharedPreferences(this).edit {
            putBoolean(getString(R.string.SP_ACTIVATEDDEVICE), true)
            putString(getString(R.string.SP_USERINFO), Gson().toJson(userinfo))
            apply()
        }

        val bundle = Bundle().apply {
            putString(FirebaseAnalytics.Param.ITEM_ID, getString(R.string.analytics_id_signin))
            putString(
                FirebaseAnalytics.Param.ITEM_NAME,
                getString(R.string.analytics_name_signin)
            )
            putString(
                FirebaseAnalytics.Param.CONTENT_TYPE,
                getString(R.string.analytics_type_text)
            )
        }

        firebaseAnalytics.logEvent(FirebaseAnalytics.Event.LOGIN, bundle)

        startActivity(Intent(this, MainActivity::class.java))
        finish()
    }
}