package com.yhjoo.dochef.ui.activities

import android.content.Intent
import android.os.Bundle
import android.view.*
import androidx.core.content.edit
import androidx.core.os.bundleOf
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task
import com.google.firebase.FirebaseNetworkException
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.auth.*
import com.google.firebase.messaging.FirebaseMessaging
import com.google.gson.Gson
import com.yhjoo.dochef.App
import com.yhjoo.dochef.R
import com.yhjoo.dochef.data.model.UserBrief
import com.yhjoo.dochef.databinding.AAccountBinding
import com.yhjoo.dochef.utils.*
import com.yhjoo.dochef.utils.RetrofitServices.AccountService
import com.yhjoo.dochef.utils.Utils.EmailValidate
import com.yhjoo.dochef.utils.Utils.NicknameValidate
import com.yhjoo.dochef.utils.Utils.PWValidate
import com.yhjoo.dochef.utils.Utils.getSharedPreferences
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.Response

class AccountActivity : BaseActivity() {
    val binding: AAccountBinding by lazy { AAccountBinding.inflate(layoutInflater) }

    private lateinit var googleSignInClient: GoogleSignInClient
    private lateinit var firebaseAnalytics: FirebaseAnalytics
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var accountService: AccountService
    private lateinit var idToken: String
    private lateinit var fcmToken: String
    private lateinit var navController:NavController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        firebaseAnalytics = FirebaseAnalytics.getInstance(this)
        firebaseAuth = FirebaseAuth.getInstance()
        googleSignInClient = GoogleSignIn.getClient(
            this,
            GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build()
        )

        FirebaseMessaging.getInstance().token
            .addOnCompleteListener { task: Task<String?> ->
                if (!task.isSuccessful) {
                    Utils.log(task.exception.toString())
                    return@addOnCompleteListener
                }

                val token = task.result
                Utils.log(token.toString())
                fcmToken = token!!
            }

        accountService = RetrofitBuilder.create(this, AccountService::class.java)

        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.account_host_fragment) as NavHostFragment
        navController = navHostFragment.navController
    }

    override fun onStart() {
        super.onStart()
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
    }

    fun checkUserInfo(idToken: String, action:Int) {
        CoroutineScope(Dispatchers.Main).launch {
            runCatching {
                accountService.checkUser(idToken, firebaseAuth.uid!!, fcmToken)
            }.onSuccess {
                if (it.code() == 409) {
                    App.showToast("닉네임을 입력해주세요.")
                    val bundle = bundleOf("token" to idToken)
                    navController.navigate(action,bundle)
                    progressOFF()
                } else startMain(it.body()!!)
            }.onFailure {
                progressOFF()
                RetrofitBuilder.defaultErrorHandler(it)
            }
        }
    }

    fun startMain(userinfo: UserBrief) {
        getSharedPreferences(this).edit {
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

        Utils.log(userinfo.toString())

        startActivity(Intent(this, MainActivity::class.java))
        finish()
    }
}