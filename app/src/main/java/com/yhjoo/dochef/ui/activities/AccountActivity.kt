package com.yhjoo.dochef.ui.activities

import android.content.Intent
import android.os.Bundle
import android.view.*
import androidx.core.content.edit
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
    companion object {
        const val RC_SIGN_IN = 9001
    }

    object MODE {
        const val SIGNIN = 0
        const val SIGNUP = 1
        const val SIGNUPNICK = 2
        const val FINDPW = 3
    }

    val binding: AAccountBinding by lazy { AAccountBinding.inflate(layoutInflater) }

    private lateinit var googleSignInClient: GoogleSignInClient
    private lateinit var firebaseAnalytics: FirebaseAnalytics
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var accountService: AccountService
    private lateinit var idToken: String
    private lateinit var fcmToken: String

    private var currentMode = MODE.SIGNIN

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

        binding.apply {
            accountSigninOk.setOnClickListener { signInWithEmailPW() }
            accountSigninGoogle.setOnClickListener { tryGoogleSignIn() }
            accountSigninSignup.setOnClickListener { startMode(MODE.SIGNUP) }
            accountSigninFindpw.setOnClickListener { startMode(MODE.FINDPW) }
            accountSignupOk.setOnClickListener { startSignUp() }
            accountSignupnickOk.setOnClickListener { signUpWithEmailPW() }
        }
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

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == RC_SIGN_IN) {
            progressON(this)
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)

            try {
                val account = task.getResult(ApiException::class.java)
                Utils.log("firebaseAuthWithGoogle:" + account!!.id)
                signInWithGoogle(account.idToken!!)
            } catch (e: ApiException) {
                Utils.log("Google sign in failed", e.toString())
                App.showToast("구글 인증 오류. 잠시 후 다시 시도해주세요.")
                progressOFF()
            }
        }
    }

    private fun signInWithEmailPW() {
        val signinEmail = binding.accountSigninEmail.text.toString()
        val signinPw = binding.accountSigninPassword.text.toString()

        when {
            Utils.emailValidation(signinEmail) == EmailValidate.NODATA || Utils.pwValidation(
                signinPw
            ) == PWValidate.NODATA ->
                App.showToast("이메일과 비밀번호를 모두 입력해주세요.")
            Utils.emailValidation(signinEmail) == EmailValidate.INVALID ->
                App.showToast("이메일 형식이 올바르지 않습니다.")
            Utils.pwValidation(signinPw) == PWValidate.LENGTH ->
                App.showToast("비밀번호 길이를 확인 해 주세요. 8자 이상, 16자 이하로 입력 해 주세요.")
            Utils.pwValidation(signinPw) == PWValidate.INVALID ->
                App.showToast("비밀번호 형식을 확인 해 주세요. 숫자, 알파벳 대소문자만 사용가능합니다.")
            else -> {
                firebaseAuth.signInWithEmailAndPassword(signinEmail, signinPw)
                    .addOnCompleteListener { authTask: Task<AuthResult> ->
                        if (!authTask.isSuccessful) {
                            val e = authTask.exception
                            if (e is FirebaseAuthException) {
                                when (e.errorCode) {
                                    "ERROR_USER_NOT_FOUND" ->
                                        App.showToast("존재하지 않는 이메일입니다. 가입 후 사용해 주세요.")
                                    "ERROR_WRONG_PASSWORD" ->
                                        App.showToast("비밀번호가 올바르지 않습니다.")
                                    "ERROR_ACCOUNT_EXISTS_WITH_DIFFERENT_CREDENTIAL" ->
                                        App.showToast(
                                            "해당 이메일주소와 연결된 다른 계정이 이미 존재합니다. 해당 이메일주소와 연결된 다른 계정을 사용하여 로그인하십시오."
                                        )
                                    else ->
                                        App.showToast("알 수 없는 오류 발생. 다시 시도해 주세요.")
                                }
                            } else App.showToast("알 수 없는 오류 발생. 다시 시도해 주세요.")
                            progressOFF()
                        } else {
                            authTask.result!!.user!!.getIdToken(true)
                                .addOnCompleteListener { task: Task<GetTokenResult> ->
                                    if (!task.isSuccessful) {
                                        progressOFF()
                                        App.showToast("알 수 없는 오류 발생. 다시 시도해 주세요")
                                    } else {
                                        idToken = task.result!!.token!!
                                        checkUserInfo(idToken)
                                    }
                                }
                        }
                    }
            }
        }
    }

    private fun tryGoogleSignIn() {
        startActivityForResult(googleSignInClient.signInIntent, RC_SIGN_IN)
    }

    private fun signInWithGoogle(googleToken: String) {
        val credential = GoogleAuthProvider.getCredential(googleToken, null)

        firebaseAuth.signInWithCredential(credential)
            .addOnCompleteListener(this) { task: Task<AuthResult?> ->
                if (task.isSuccessful) {
                    val user = firebaseAuth.currentUser
                    user!!.getIdToken(true)
                        .addOnCompleteListener { tokenTask: Task<GetTokenResult> ->
                            if (!tokenTask.isSuccessful) {
                                progressOFF()
                                App.showToast("알 수 없는 오류가 발생. 다시 시도해 주세요")
                            } else {
                                idToken = tokenTask.result!!.token!!
                                checkUserInfo(idToken)
                            }
                        }
                } else {
                    task.exception?.printStackTrace()
                    App.showToast("알 수 없는 오류 발생. 다시 시도해 주세요.")
                }
            }
    }

    private fun startSignUp() {
        val email = binding.accountSignupEmail.text.toString()
        val pw = binding.accountSignupPassword.text.toString()

        when {
            Utils.emailValidation(email) == EmailValidate.NODATA || Utils.pwValidation(email) == PWValidate.NODATA ->
                App.showToast("이메일과 비밀번호를 모두 입력해주세요.")
            Utils.emailValidation(email) == EmailValidate.INVALID ->
                App.showToast("이메일 형식이 올바르지 않습니다.")
            Utils.pwValidation(pw) == PWValidate.LENGTH ->
                App.showToast("비밀번호 길이를 확인 해 주세요. 8자 이상, 16자 이하로 입력 해 주세요.")
            Utils.pwValidation(pw) == PWValidate.INVALID ->
                App.showToast("비밀번호 형식을 확인 해 주세요. 숫자, 알파벳 대소문자만 사용가능합니다.")
            else -> {
                progressON(this)
                firebaseAuth.createUserWithEmailAndPassword(email, pw)
                    .addOnCompleteListener { authTask: Task<AuthResult> ->
                        if (!authTask.isSuccessful) {
                            progressOFF()

                            when (authTask.exception) {
                                is FirebaseAuthException -> {
                                    val fbae =
                                        (authTask.exception as FirebaseAuthException).errorCode
                                    if ("ERROR_EMAIL_ALREADY_IN_USE" == fbae)
                                        App.showToast("이미 가입되있는 이메일입니다.")
                                    else
                                        App.showToast("알 수 없는 오류 발생. 다시 시도해 주세요.")
                                }
                                is FirebaseNetworkException ->
                                    App.showToast("네트워크 상태를 확인해주세요.")
                                else ->
                                    App.showToast("알 수 없는 오류가 발생. 다시 시도해 주세요")
                            }
                        } else {
                            authTask.result!!.user!!.getIdToken(true)
                                .addOnCompleteListener { task: Task<GetTokenResult> ->
                                    if (!task.isSuccessful) {
                                        progressOFF()
                                        App.showToast("알 수 없는 오류가 발생. 다시 시도해 주세요")
                                    } else {
                                        idToken = task.result!!.token!!
                                        checkUserInfo(idToken)
                                    }
                                }
                        }
                    }
            }
        }
    }

    private fun signUpWithEmailPW() {
        val nickname = binding.accountSignupnickNickname.text.toString()

        when {
            Utils.nicknameValidate(nickname) == NicknameValidate.NODATA ->
                App.showToast("닉네임을 입력 해 주세요.")
            Utils.nicknameValidate(nickname) == NicknameValidate.LENGTH ->
                App.showToast("닉네임의 길이를 확인 해 주세요. 6자 이상, 10자 이하로 입력해주세요")
            Utils.nicknameValidate(nickname) == NicknameValidate.INVALID ->
                App.showToast("사용할 수 없는 닉네임입니다. 숫자, 알파벳 대소문자, 한글만 사용가능합니다.")
            else -> {
                progressON(this)
                CoroutineScope(Dispatchers.Main).launch {
                    runCatching {
                        accountService.createUser(idToken, fcmToken, firebaseAuth.uid!!, nickname)
                    }.onSuccess {
                        if (it.code() == 403)
                            App.showToast("이미 존재하는 닉네임입니다.")
                        else {
                            App.showToast("회원 가입 되었습니다.")
                            startMain(it.body()!!)
                        }
                    }.onFailure {
                        progressOFF()
                        RetrofitBuilder.defaultErrorHandler(it)
                    }
                }
            }
        }
    }

    private fun checkUserInfo(idToken: String) {
        CoroutineScope(Dispatchers.Main).launch {
            runCatching {
                accountService.checkUser(idToken, firebaseAuth.uid!!, fcmToken)
            }.onSuccess {
                if (it.code() == 409) {
                    App.showToast("닉네임을 입력해주세요.")
                    startMode(MODE.SIGNUPNICK)
                    progressOFF()
                } else startMain(it.body()!!)
            }.onFailure {
                progressOFF()
                RetrofitBuilder.defaultErrorHandler(it)
            }
        }
    }

    private fun startMode(mode: Int) {
        binding.apply {
            accountSigninGroup.visibility = View.GONE
            accountSignupGroup.visibility = View.GONE
            accountSignupnickGroup.visibility = View.GONE
            accountFindpwGroup.visibility = View.GONE
        }

        when (mode) {
            MODE.SIGNUP -> {
                currentMode = MODE.SIGNUP
                binding.accountSignupGroup.visibility = View.VISIBLE

                val bundle = Bundle().apply {
                    putString(
                        FirebaseAnalytics.Param.ITEM_ID,
                        getString(R.string.analytics_id_signup)
                    )
                    putString(
                        FirebaseAnalytics.Param.ITEM_NAME,
                        getString(R.string.analytics_name_signup)
                    )
                    putString(
                        FirebaseAnalytics.Param.CONTENT_TYPE,
                        getString(R.string.analytics_type_text)
                    )
                }
                firebaseAnalytics.logEvent(FirebaseAnalytics.Event.SIGN_UP, bundle)
            }
            MODE.SIGNUPNICK -> {
                currentMode = MODE.SIGNUPNICK
                binding.accountSignupnickGroup.visibility = View.VISIBLE
            }
            MODE.FINDPW -> {
                currentMode = MODE.FINDPW
                binding.accountFindpwGroup.visibility = View.VISIBLE
            }
        }
    }

    private fun startMain(userinfo: UserBrief) {
        getSharedPreferences(this).edit {
            putBoolean(getString(R.string.SP_ACTIVATEDDEVICE), true)
            putString(getString(R.string.SP_USERINFO), Gson().toJson(userinfo))
            apply()
        }

        Utils.log(userinfo.toString())

        startActivity(Intent(this, MainActivity::class.java))
        finish()
    }
}