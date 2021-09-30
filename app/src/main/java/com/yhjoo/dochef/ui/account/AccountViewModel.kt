package com.yhjoo.dochef.ui.account

import android.app.Application
import android.content.Intent
import android.os.Bundle
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
import com.yhjoo.dochef.data.repository.AccountRepository
import com.yhjoo.dochef.utils.DatastoreUtil
import com.yhjoo.dochef.utils.OtherUtil
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class AccountViewModel(
    private val application: Application,
    private val accountRepository: AccountRepository
) : ViewModel() {
    companion object CONSTANTS {
        object PHASE {
            const val SIGNIN_EMAIL = 0
            const val SIGNIN_GOOGLE = 1
            const val SIGNUP_EMAIL = 2
            const val SIGNUP_NICKNAME = 3
            const val ACQUIRE_TOKEN = 4
            const val CHECK_USERINFO = 5
            const val FIND_PW = 6
        }
    }
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

    private var _phaseError = MutableLiveData<Pair<Int, String>>()
    private var _phaseFindPWComplete = MutableLiveData<Boolean>()
    private var _phaseAllComplete = MutableLiveData<Boolean>()

    val phaseError: LiveData<Pair<Int, String>>
        get() = _phaseError
    val phaseFindPWComplete: LiveData<Boolean>
        get() = _phaseFindPWComplete
    val phaseAllComplete: LiveData<Boolean>
        get() = _phaseAllComplete

    init {
        FirebaseMessaging.getInstance().token
            .addOnCompleteListener { task: Task<String?> ->
                if (!task.isSuccessful) {
                    OtherUtil.log(task.exception.toString())
                    return@addOnCompleteListener
                } else
                    fcmToken = task.result!!
            }
    }

    fun signInWithGoogle(idToken: String) {
        viewModelScope.launch {
            val credential = GoogleAuthProvider.getCredential(idToken, null)

            firebaseAuth
                .signInWithCredential(credential)
                .addOnCompleteListener { task: Task<AuthResult?> ->
                    if (task.isSuccessful) {
                        getFirebaseUserToken()
                    } else {
                        task.exception?.printStackTrace()

                        val message = "알 수 없는 오류 발생. 다시 시도해 주세요."

                        _phaseError.value = Pair(
                            PHASE.SIGNIN_GOOGLE,
                            message
                        )
                    }
                }
        }
    }

    fun signInWithEmail(email: String, pw: String) {
        viewModelScope.launch {
            firebaseAuth
                .signInWithEmailAndPassword(email, pw)
                .addOnCompleteListener { task: Task<AuthResult> ->
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

                        _phaseError.value = Pair(
                            PHASE.SIGNIN_EMAIL,
                            message
                        )
                    }
                }
        }
    }

    fun signUpWithEmail(email: String, pw: String) {
        viewModelScope.launch {
            firebaseAuth
                .createUserWithEmailAndPassword(email, pw)
                .addOnCompleteListener { task: Task<AuthResult> ->
                    if (task.isSuccessful) {
                        getFirebaseUserToken()
                    } else {
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

                        _phaseError.value = Pair(
                            PHASE.SIGNUP_EMAIL,
                            message
                        )
                    }
                }
        }
    }

    fun signUpWithNickname(nickname: String) {
        viewModelScope.launch {
            accountRepository.createUser(
                firebaseUserToken,
                fcmToken,
                firebaseAuth.uid!!,
                nickname
            ).collect {
                if (it.code() == 403) {
                    _phaseError.value = Pair(
                        PHASE.SIGNUP_NICKNAME,
                        "이미 존재하는 닉네임입니다."
                    )
                } else {
                    val bundle = bundleOf(
                        Pair(
                            FirebaseAnalytics.Param.ITEM_ID,
                            Constants.ANALYTICS.ID.SIGNUP
                        ),
                        Pair(
                            FirebaseAnalytics.Param.ITEM_NAME,
                            Constants.ANALYTICS.NAME.SIGNUP
                        )
                    )
                    analyticsLogEvent(FirebaseAnalytics.Event.SIGN_UP, bundle)

                    val userInfo = it.body()!!
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

                    _phaseAllComplete.value = true
                }
            }
        }
    }

    fun getFirebaseUserToken() {
        firebaseAuth.currentUser!!
            .getIdToken(true)
            .addOnCompleteListener { task: Task<GetTokenResult> ->
                if (task.isSuccessful) {
                    firebaseUserToken = task.result!!.token!!
                    checkUserInfo()
                } else {
                    task.exception?.printStackTrace()

                    val message = "토큰을 얻어오는데 실패했습니다."
                    _phaseError.value = Pair(
                        PHASE.ACQUIRE_TOKEN,
                        message
                    )
                }
            }
    }

    fun checkUserInfo() {
        viewModelScope.launch {
            accountRepository.checkUser(
                firebaseUserToken,
                firebaseAuth.uid!!,
                fcmToken
            ).collect {
                if (it.code() == 409) {
                    val message = "닉네임을 입력해주세요."
                    _phaseError.value = Pair(
                        PHASE.CHECK_USERINFO,
                        message
                    )
                } else {
                    val userInfo = it.body()!!
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

                    _phaseAllComplete.value = true
                }
            }
        }
    }

    fun sendPasswordResetEmail(email: String) {
        firebaseAuth
            .sendPasswordResetEmail(email)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    _phaseFindPWComplete.value = true
                } else {
                    task.exception?.printStackTrace()

                    val message = "비밀번호 재설정에 실패했습니다."
                    _phaseError.value = Pair(
                        PHASE.FIND_PW,
                        message
                    )
                }
            }
    }

    fun analyticsLogEvent(event: String, bundle: Bundle) {
        firebaseAnalytics.logEvent(event, bundle)
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