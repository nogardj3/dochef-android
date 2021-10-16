package com.yhjoo.dochef.ui.account

import android.app.Application
import android.content.Intent
import androidx.core.content.edit
import androidx.core.os.bundleOf
import androidx.lifecycle.*
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.tasks.Task
import com.google.firebase.FirebaseNetworkException
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.auth.*
import com.google.firebase.messaging.FirebaseMessaging
import com.google.gson.Gson
import com.yhjoo.dochef.Constants
import com.yhjoo.dochef.R
import com.yhjoo.dochef.data.model.UserBrief
import com.yhjoo.dochef.data.repository.AccountRepository
import com.yhjoo.dochef.utils.DatastoreUtil
import com.yhjoo.dochef.utils.OtherUtil
import com.yhjoo.dochef.utils.ValidateUtil
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class AccountViewModel(
    private val application: Application,
    private val accountRepository: AccountRepository
) : ViewModel() {
    val googleSigninIntent: Intent by lazy {
        GoogleSignIn.getClient(
            application.applicationContext,
            GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
//                .requestIdToken(getString(R.string.default_web_client_id))
                .requestIdToken("227618773978-c5ptgsjltcrv8hl1dmgci6rnedd8ene9.apps.googleusercontent.com")
                .requestEmail()
                .build()
        ).signInIntent
    }
    private val firebaseAnalytics: FirebaseAnalytics by lazy {
        FirebaseAnalytics.getInstance(application.applicationContext)
    }
    private val firebaseAuth: FirebaseAuth by lazy {
        FirebaseAuth.getInstance()
    }
    private lateinit var fcmToken: String
    private lateinit var firebaseUserToken: String

    private var _eventResult = MutableSharedFlow<Pair<Any, String?>>()
    val eventResult = _eventResult.asSharedFlow()

    init {
        FirebaseMessaging.getInstance().token
            .addOnCompleteListener { task: Task<String?> ->
                if (!task.isSuccessful) {
                    OtherUtil.log(task.exception.toString())
                    return@addOnCompleteListener
                } else fcmToken = task.result!!
            }
    }

    fun clickFindPw(email: String) = viewModelScope.launch {
        val validateResult = ValidateUtil.emailValidate(email)

        if (validateResult.first == ValidateUtil.EmailResult.VALID) {
            _eventResult.emit(Pair(Events.FindPW.WAIT, null))

            firebaseAuth
                .sendPasswordResetEmail(email)
                .addOnCompleteListener { task ->
                    viewModelScope.launch {
                        if (task.isSuccessful) {
                            _eventResult.emit(Pair(Events.FindPW.COMPLETE, null))
                        } else {
                            task.exception?.printStackTrace()

                            val message = "이메일 전송에 실패했습니다."
                            _eventResult.emit(Pair(Events.FindPW.ERROR_EMAIL, message))
                        }

                    }
                }
        } else
            _eventResult.emit(Pair(Events.FindPW.ERROR_EMAIL, validateResult.second))
    }

    fun clickSignInWithEmail(email: String, pw: String) = viewModelScope.launch {
        val emailValidateResult = ValidateUtil.emailValidate(email)
        val pwValidateResult = ValidateUtil.pwValidate(pw)

        when {
            emailValidateResult.first != ValidateUtil.EmailResult.VALID -> {
                _eventResult.emit(
                    Pair(
                        Events.SignInEmail.ERROR_EMAIL,
                        emailValidateResult.second
                    )
                )
            }
            pwValidateResult.first != ValidateUtil.PwResult.VALID -> {
                _eventResult.emit(
                    Pair(
                        Events.SignInEmail.ERROR_PW,
                        pwValidateResult.second
                    )
                )
            }
            else -> {
                _eventResult.emit(Pair(Events.SignInEmail.WAIT, null))

                firebaseAuth
                    .signInWithEmailAndPassword(email, pw)
                    .addOnCompleteListener { task: Task<AuthResult> ->
                        viewModelScope.launch {
                            if (task.isSuccessful)
                                getFirebaseUserToken()
                            else {
                                task.exception?.printStackTrace()

                                val e = task.exception
                                val message: String = if (e is FirebaseAuthException) {
                                    when (e.errorCode) {
                                        "ERROR_USER_NOT_FOUND" ->
                                            "존재하지 않는 이메일입니다. 가입 후 사용해 주세요."
                                        "ERROR_WRONG_PASSWORD" ->
                                            "비밀번호가 올바르지 않습니다."
                                        "ERROR_ACCOUNT_EXISTS_WITH_DIFFERENT_CREDENTIAL" ->
                                            "해당 이메일주소와 연결된 다른 계정이 이미 존재합니다. 해당 이메일주소와 연결된 다른 계정을 사용하여 로그인하십시오."
                                        else ->
                                            "계정 인증 오류 발생. 다시 시도해 주세요."
                                    }
                                } else "알 수 없는 오류 발생. 다시 시도해 주세요."

                                _eventResult.emit(
                                    Pair(
                                        Events.SignInEmail.ERROR_AUTH,
                                        message
                                    )
                                )
                            }
                        }
                    }
            }
        }
    }

    fun clickSignInWithGoogle(idToken: String) = viewModelScope.launch {
        val credential = GoogleAuthProvider.getCredential(idToken, null)

        firebaseAuth
            .signInWithCredential(credential)
            .addOnCompleteListener { task: Task<AuthResult?> ->
                viewModelScope.launch {
                    if (task.isSuccessful) getFirebaseUserToken()
                    else {
                        task.exception?.printStackTrace()

                        val message = "알 수 없는 오류 발생. 다시 시도해 주세요."

                        _eventResult.emit(
                            Pair(
                                Events.SignInGoogle.ERROR_GOOGLE,
                                message
                            )
                        )
                    }
                }
            }
    }

    fun clickSignUpWithEmail(email: String, pw: String) = viewModelScope.launch {
        val emailValidateResult = ValidateUtil.emailValidate(email)
        val pwValidateResult = ValidateUtil.pwValidate(pw)

        when {
            emailValidateResult.first != ValidateUtil.EmailResult.VALID -> {
                _eventResult.emit(
                    Pair(
                        Events.SignUpEmail.ERROR_EMAIL,
                        emailValidateResult.second
                    )
                )
            }
            pwValidateResult.first != ValidateUtil.PwResult.VALID -> {
                _eventResult.emit(
                    Pair(
                        Events.SignUpEmail.ERROR_PW,
                        pwValidateResult.second
                    )
                )
            }
            else -> {
                _eventResult.emit(Pair(Events.SignUpEmail.WAIT, null))

                firebaseAuth
                    .createUserWithEmailAndPassword(email, pw)
                    .addOnCompleteListener { task: Task<AuthResult> ->
                        viewModelScope.launch {
                            if (task.isSuccessful)
                                getFirebaseUserToken()
                            else {
                                task.exception?.printStackTrace()

                                val message: String = when (task.exception) {
                                    is FirebaseAuthException -> {
                                        val fbae =
                                            (task.exception as FirebaseAuthException).errorCode
                                        if ("ERROR_EMAIL_ALREADY_IN_USE" == fbae)
                                            "이미 가입되있는 이메일입니다."
                                        else
                                            "알 수 없는 오류 발생. 다시 시도해 주세요."
                                    }
                                    is FirebaseNetworkException ->
                                        "네트워크 상태를 확인해주세요."
                                    else ->
                                        "알 수 없는 오류가 발생. 다시 시도해 주세요"
                                }

                                _eventResult.emit(
                                    Pair(
                                        Events.SignUpEmail.ERROR_AUTH,
                                        message
                                    )
                                )
                            }
                        }
                    }
            }
        }
    }

    fun clickSignUpWithNickname(nickname: String) = viewModelScope.launch {
        val validateResult = ValidateUtil.nicknameValidate(nickname)

        if (validateResult.first == ValidateUtil.NicknameResult.VALID) {
            _eventResult.emit(Pair(Events.SignUpNickname.WAIT, null))

            accountRepository.createUser(
                firebaseUserToken,
                fcmToken,
                firebaseAuth.uid!!,
                nickname
            ).collect {
                if (it.code() == 403) {
                    _eventResult.emit(
                        Pair(
                            Events.SignUpNickname.ERROR,
                            "이미 존재하는 닉네임입니다."
                        )
                    )
                } else {
                    firebaseAnalytics.logEvent(
                        FirebaseAnalytics.Event.SIGN_UP, bundleOf(
                            Pair(
                                FirebaseAnalytics.Param.ITEM_ID,
                                Constants.ANALYTICS.ID.SIGNUP
                            ),
                            Pair(
                                FirebaseAnalytics.Param.ITEM_NAME,
                                Constants.ANALYTICS.NAME.SIGNUP
                            )
                        )
                    )
                    allComplete(it.body()!!)
                }
            }
        } else
            _eventResult.emit(
                Pair(
                    Events.SignUpNickname.ERROR,
                    validateResult.second
                )
            )
    }

    private fun getFirebaseUserToken() = viewModelScope.launch {
        firebaseAuth.currentUser!!
            .getIdToken(true)
            .addOnCompleteListener { task: Task<GetTokenResult> ->
                viewModelScope.launch {
                    if (task.isSuccessful) {
                        firebaseUserToken = task.result!!.token!!
                        checkUserInfo()
                    } else {
                        task.exception?.printStackTrace()

                        val message = "토큰을 얻어오는데 실패했습니다."

                        _eventResult.emit(
                            Pair(
                                Events.Error.ERROR_REQUIRE_TOKEN,
                                message
                            )
                        )
                    }
                }
            }
    }

    private fun checkUserInfo() = viewModelScope.launch {
        accountRepository.checkUser(
            firebaseUserToken,
            firebaseAuth.uid!!,
            fcmToken
        ).collect {
            if (it.code() == 409) {
                val message = "회원가입을 시작합니다. 닉네임을 입력해주세요."

                _eventResult.emit(
                    Pair(
                        Events.Error.ERROR_REQUIRE_SIGNUP,
                        message
                    )
                )
            } else
                allComplete(it.body()!!)
        }
    }

    private fun allComplete(userInfo: UserBrief) = viewModelScope.launch {
        OtherUtil.log(userInfo.toString())
        DatastoreUtil.getSharedPreferences(application.applicationContext).edit {
            putBoolean(
                application.applicationContext.getString(R.string.SP_ACTIVATEDDEVICE),
                true
            )
            putString(
                application.applicationContext.getString(R.string.SP_USERINFO),
                Gson().toJson(userInfo)
            )
            apply()
        }

        firebaseAnalytics.logEvent(
            FirebaseAnalytics.Event.LOGIN,
            bundleOf(
                Pair(FirebaseAnalytics.Param.ITEM_ID, Constants.ANALYTICS.ID.SIGNIN),
                Pair(FirebaseAnalytics.Param.ITEM_NAME, Constants.ANALYTICS.NAME.SIGNIN)
            )
        )

        _eventResult.emit(Pair(Events.Complete.COMPLETE,null))
    }

    sealed class Events {
        enum class FindPW {
            COMPLETE, WAIT, ERROR_EMAIL
        }

        enum class SignInEmail {
            WAIT, ERROR_EMAIL, ERROR_PW, ERROR_AUTH
        }

        enum class SignInGoogle {
            ERROR_GOOGLE
        }

        enum class SignUpEmail {
            WAIT, ERROR_EMAIL, ERROR_PW, ERROR_AUTH
        }

        enum class SignUpNickname {
            WAIT, ERROR
        }

        enum class Error {
            ERROR_REQUIRE_TOKEN, ERROR_REQUIRE_SIGNUP
        }

        enum class Complete{
            COMPLETE
        }
    }
}

class AccountViewModelFactory(
    private val application: Application,
    private val accountRepository: AccountRepository
) :
    ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(AccountViewModel::class.java)) {
            return AccountViewModel(
                application,
                accountRepository
            ) as T
        }
        throw IllegalArgumentException("Unknown View Model class")
    }
}