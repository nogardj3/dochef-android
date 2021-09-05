package com.yhjoo.dochef.activities

import android.content.Intent
import android.os.Bundle
import android.preference.PreferenceManager
import android.view.*
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
import com.yhjoo.dochef.App.Companion.appInstance
import com.yhjoo.dochef.R
import com.yhjoo.dochef.databinding.AAccountBinding
import com.yhjoo.dochef.utils.RxRetrofitServices.AccountService
import com.yhjoo.dochef.data.model.UserBrief
import com.yhjoo.dochef.utils.*
import com.yhjoo.dochef.utils.Utils.EmailValidate
import com.yhjoo.dochef.utils.Utils.NicknameValidate
import com.yhjoo.dochef.utils.Utils.PWValidate
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import retrofit2.Response

class AccountActivity : BaseActivity() {
    private val RC_SIGN_IN = 9001

    enum class Mode {
        SIGNIN, SIGNUP, SIGNUPNICK, FINDPW
    }

    var binding: AAccountBinding? = null
    var mGoogleSignInClient: GoogleSignInClient? = null
    var mFirebaseAnalytics: FirebaseAnalytics? = null
    var mAuth: FirebaseAuth? = null
    var accountService: AccountService? = null
    var current_mode = Mode.SIGNIN
    var idToken: String? = null
    var fcmToken: String? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = AAccountBinding.inflate(layoutInflater)
        setContentView(binding!!.root)
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this)
        mAuth = FirebaseAuth.getInstance()
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso)
        FirebaseMessaging.getInstance().token
            .addOnCompleteListener { task: Task<String?> ->
                if (!task.isSuccessful) {
                    Utils.log(task.exception)
                    return@addOnCompleteListener
                }
                val token = task.result
                Utils.log(token)
                fcmToken = token
            }
        accountService = RxRetrofitBuilder.create(this, AccountService::class.java)
        binding!!.accountSigninOk.setOnClickListener { v: View? -> signInWithEmailPW(v) }
        binding!!.accountSigninGoogle.setOnClickListener { v: View? -> tryGoogleSignIn(v) }
        binding!!.accountSigninSignup.setOnClickListener { v: View? -> startMode(Mode.SIGNUP, "") }
        binding!!.accountSigninFindpw.setOnClickListener { v: View? -> startMode(Mode.FINDPW, "") }
        binding!!.accountSignupOk.setOnClickListener { v: View? -> startSignUp(v) }
        binding!!.accountSignupnickOk.setOnClickListener { v: View? -> signUpWithEmailPW(v) }
    }

    override fun onStart() {
        super.onStart()
        val bundle = Bundle()
        bundle.putString(FirebaseAnalytics.Param.ITEM_ID, getString(R.string.analytics_id_signin))
        bundle.putString(
            FirebaseAnalytics.Param.ITEM_NAME,
            getString(R.string.analytics_name_signin)
        )
        bundle.putString(
            FirebaseAnalytics.Param.CONTENT_TYPE,
            getString(R.string.analytics_type_text)
        )
        mFirebaseAnalytics!!.logEvent(FirebaseAnalytics.Event.LOGIN, bundle)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == RC_SIGN_IN) {
            progressON(this)
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            try {
                val account = task.getResult(ApiException::class.java)
                Utils.log("firebaseAuthWithGoogle:" + account!!.id)
                signInWithGoogle(account.idToken)
            } catch (e: ApiException) {
                Utils.log("Google sign in failed", e.toString())
                appInstance!!.showToast("구글 인증 오류. 잠시 후 다시 시도해주세요.")
                progressOFF()
            }
        } else {
            Utils.log("Something wrong")
            progressOFF()
        }
    }

    fun signInWithEmailPW(v: View?) {
        val signin_email = binding!!.accountSigninEmail.text.toString()
        val signin_pw = binding!!.accountSigninPassword.text.toString()
        if (Utils.emailValidation(signin_email) == EmailValidate.NODATA
            || Utils.pwValidation(signin_pw) == PWValidate.NODATA
        ) appInstance.showToast("이메일과 비밀번호를 모두 입력해주세요.") else if (Utils.emailValidation(
                signin_email
            ) == EmailValidate.INVALID
        ) appInstance.showToast("이메일 형식이 올바르지 않습니다.") else if (Utils.pwValidation(signin_pw) == PWValidate.SHORT
            || Utils.pwValidation(signin_pw) == PWValidate.LONG
        ) appInstance.showToast("비밀번호 길이를 확인 해 주세요. 8자 이상, 16자 이하로 입력 해 주세요.") else if (Utils.pwValidation(
                signin_pw
            ) == PWValidate.INVALID
        ) appInstance.showToast("비밀번호 형식을 확인 해 주세요. 숫자, 알파벳 대소문자만 사용가능합니다.") else {
            mAuth!!.signInWithEmailAndPassword(signin_email, signin_pw)
                .addOnCompleteListener { authTask: Task<AuthResult> ->
                    if (!authTask.isSuccessful) {
                        val e = authTask.exception
                        if (e is FirebaseAuthException) {
                            val fbae = e.errorCode
                            when (fbae) {
                                "ERROR_USER_NOT_FOUND" -> appInstance!!.showToast("존재하지 않는 이메일입니다. 가입 후 사용해 주세요.")
                                "ERROR_WRONG_PASSWORD" -> appInstance!!.showToast("비밀번호가 올바르지 않습니다.")
                                "ERROR_ACCOUNT_EXISTS_WITH_DIFFERENT_CREDENTIAL" -> appInstance!!.showToast(
                                    "해당 이메일주소와 연결된 다른 계정이 이미 존재합니다. 해당 이메일주소와 연결된 다른 계정을 사용하여 로그인하십시오."
                                )
                                else -> appInstance!!.showToast("알 수 없는 오류 발생. 다시 시도해 주세요.")
                            }
                        } else appInstance!!.showToast("알 수 없는 오류 발생. 다시 시도해 주세요.")
                        progressOFF()
                    } else {
                        authTask.result!!.user!!.getIdToken(true)
                            .addOnCompleteListener { task: Task<GetTokenResult> ->
                                if (!task.isSuccessful) {
                                    progressOFF()
                                    appInstance!!.showToast("알 수 없는 오류 발생. 다시 시도해 주세요")
                                } else {
                                    idToken = task.result!!.token
                                    checkUserInfo(idToken)
                                }
                            }
                    }
                }
        }
    }

    fun tryGoogleSignIn(v: View?) {
        val signInIntent = mGoogleSignInClient!!.signInIntent
        startActivityForResult(signInIntent, RC_SIGN_IN)
    }

    fun signInWithGoogle(googleToken: String?) {
        val credential = GoogleAuthProvider.getCredential(googleToken, null)
        mAuth!!.signInWithCredential(credential)
            .addOnCompleteListener(this) { task: Task<AuthResult?> ->
                if (task.isSuccessful) {
                    Utils.log("success")
                    val user = mAuth!!.currentUser
                    user!!.getIdToken(true)
                        .addOnCompleteListener { tt: Task<GetTokenResult> ->
                            if (!tt.isSuccessful) {
                                progressOFF()
                                appInstance!!.showToast("알 수 없는 오류가 발생. 다시 시도해 주세요")
                            } else {
                                idToken = tt.result!!.token
                                checkUserInfo(idToken)
                            }
                        }
                } else {
                    Utils.log(task.exception.toString())
                    appInstance!!.showToast("알 수 없는 오류 발생. 다시 시도해 주세요.")
                }
            }
    }

    fun startSignUp(v: View?) {
        val email = binding!!.accountSignupEmail.text.toString()
        val pw = binding!!.accountSignupPassword.text.toString()
        if (Utils.emailValidation(email) == EmailValidate.NODATA || Utils.pwValidation(email) == PWValidate.NODATA) appInstance!!.showToast(
            "이메일과 비밀번호를 모두 입력해주세요."
        ) else if (Utils.emailValidation(email) == EmailValidate.INVALID) appInstance!!.showToast("이메일 형식이 올바르지 않습니다.") else if (Utils.pwValidation(
                pw
            ) == PWValidate.SHORT || Utils.pwValidation(pw) == PWValidate.LONG
        ) appInstance!!.showToast("비밀번호 길이를 확인 해 주세요. 8자 이상, 16자 이하로 입력 해 주세요.") else if (Utils.pwValidation(
                pw
            ) == PWValidate.INVALID
        ) appInstance!!.showToast("비밀번호 형식을 확인 해 주세요. 숫자, 알파벳 대소문자만 사용가능합니다.") else {
            progressON(this)
            mAuth!!.createUserWithEmailAndPassword(email, pw)
                .addOnCompleteListener { authTask: Task<AuthResult> ->
                    if (!authTask.isSuccessful) {
                        progressOFF()
                        val e = authTask.exception
                        if (e is FirebaseAuthException) {
                            val fbae = e.errorCode
                            if ("ERROR_EMAIL_ALREADY_IN_USE" == fbae) appInstance!!.showToast("이미 가입되있는 이메일입니다.") else appInstance!!.showToast(
                                "알 수 없는 오류 발생. 다시 시도해 주세요."
                            )
                        } else if (e is FirebaseNetworkException) {
                            appInstance!!.showToast("네트워크 상태를 확인해주세요.")
                        } else {
                            appInstance!!.showToast("알 수 없는 오류가 발생. 다시 시도해 주세요")
                        }
                    } else {
                        authTask.result!!.user!!.getIdToken(true)
                            .addOnCompleteListener { task: Task<GetTokenResult> ->
                                if (!task.isSuccessful) {
                                    progressOFF()
                                    appInstance!!.showToast("알 수 없는 오류가 발생. 다시 시도해 주세요")
                                } else {
                                    idToken = task.result!!.token
                                    checkUserInfo(idToken)
                                }
                            }
                    }
                }
        }
    }

    fun signUpWithEmailPW(v: View?) {
        val nickname = binding!!.accountSignupnickNickname.text.toString()
        if (Utils.nicknameValidate(nickname) == NicknameValidate.NODATA) appInstance!!.showToast("닉네임을 입력 해 주세요.") else if (Utils.nicknameValidate(
                nickname
            ) == NicknameValidate.SHORT ||
            Utils.nicknameValidate(nickname) == NicknameValidate.LONG
        ) appInstance!!.showToast("닉네임의 길이를 확인 해 주세요. 6자 이상, 10자 이하로 입력해주세요") else if (Utils.nicknameValidate(
                nickname
            ) == NicknameValidate.INVALID
        ) appInstance!!.showToast("사용할 수 없는 닉네임입니다. 숫자, 알파벳 대소문자, 한글만 사용가능합니다.") else {
            progressON(this)
            compositeDisposable!!.add(
                accountService!!.createUser(idToken, fcmToken, mAuth!!.uid, nickname)
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe({ response: Response<UserBrief?>? ->
                        if (response!!.code() == 403) appInstance!!.showToast("이미 존재하는 닉네임입니다.") else {
                            appInstance!!.showToast("회원 가입 되었습니다.")
                            startMain(response.body())
                        }
                    }) { throwable: Throwable ->
                        throwable.printStackTrace()
                        progressOFF()
                    }
            )
        }
    }

    fun checkUserInfo(idToken: String?) {
        compositeDisposable!!.add(
            accountService!!.checkUser(idToken, mAuth!!.uid, fcmToken)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ response: Response<UserBrief?>? ->
                    if (response!!.code() == 409) {
                        appInstance!!.showToast("닉네임을 입력해주세요.")
                        startMode(Mode.SIGNUPNICK, idToken)
                        progressOFF()
                    } else startMain(response.body())
                }) { e: Throwable ->
                    e.printStackTrace()
                    progressOFF()
                }
        )
    }

    fun startMode(mode: Mode, token: String?) {
        binding!!.accountSigninGroup.visibility = View.GONE
        binding!!.accountSignupGroup.visibility = View.GONE
        binding!!.accountSignupnickGroup.visibility = View.GONE
        binding!!.accountFindpwGroup.visibility = View.GONE
        if (mode == Mode.SIGNUP) {
            current_mode = Mode.SIGNUP
            binding!!.accountSignupGroup.visibility = View.VISIBLE
            val bundle = Bundle()
            bundle.putString(
                FirebaseAnalytics.Param.ITEM_ID,
                getString(R.string.analytics_id_signup)
            )
            bundle.putString(
                FirebaseAnalytics.Param.ITEM_NAME,
                getString(R.string.analytics_name_signup)
            )
            bundle.putString(
                FirebaseAnalytics.Param.CONTENT_TYPE,
                getString(R.string.analytics_type_text)
            )
            mFirebaseAnalytics!!.logEvent(FirebaseAnalytics.Event.SIGN_UP, bundle)
        } else if (mode == Mode.SIGNUPNICK) {
            current_mode = Mode.SIGNUPNICK
            binding!!.accountSignupnickGroup.visibility = View.VISIBLE
        } else if (mode == Mode.FINDPW) {
            current_mode = Mode.FINDPW
            binding!!.accountFindpwGroup.visibility = View.VISIBLE
        }
    }

    fun startMain(userinfo: UserBrief?) {
        Utils.log("HELLO")
        val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(applicationContext)
        val editor = sharedPreferences.edit()
        val gson = Gson()
        editor.putBoolean(getString(R.string.SP_ACTIVATEDDEVICE), true)
        editor.putString(getString(R.string.SP_USERINFO), gson.toJson(userinfo))
        editor.apply()
        Utils.log(userinfo.toString())
        Utils.log(gson.toJson(userinfo))
        startActivity(Intent(this, MainActivity::class.java))
        finish()
    }
}