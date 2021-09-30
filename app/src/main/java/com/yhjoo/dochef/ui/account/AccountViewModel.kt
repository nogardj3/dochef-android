package com.yhjoo.dochef.ui.account

import android.app.Activity
import android.os.Bundle
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.tasks.Task
import com.google.firebase.FirebaseNetworkException
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.auth.*
import com.yhjoo.dochef.App
import com.yhjoo.dochef.Constants
import com.yhjoo.dochef.data.model.UserBrief
import com.yhjoo.dochef.data.repository.AccountRepository
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class AccountViewModel(
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
        }
    }

    val googleClient = MutableLiveData<GoogleSignInClient>()
    val firebaseAnalytics = MutableLiveData<FirebaseAnalytics>()
    val firebaseAuth = MutableLiveData<FirebaseAuth>()
    val firebaseUserToken = MutableLiveData<String>()
    val fcmToken = MutableLiveData<String>()

    val phaseError = MutableLiveData<Pair<Int, String>>()
    val phaseAllComplete = MutableLiveData<UserBrief>()

    fun signInWithGoogle(activity: Activity, idToken: String) {
        viewModelScope.launch {
            val credential = GoogleAuthProvider.getCredential(idToken, null)

            firebaseAuth.value!!
                .signInWithCredential(credential)
                .addOnCompleteListener(activity) { task: Task<AuthResult?> ->
                    if (task.isSuccessful) {
                        getFirebaseUserToken()
                    } else {
                        task.exception?.printStackTrace()

                        val message = "알 수 없는 오류 발생. 다시 시도해 주세요."

                        phaseError.value = Pair(
                            PHASE.SIGNIN_GOOGLE,
                            message
                        )
                    }
                }
        }
    }

    fun signInWithEmail(email: String, pw: String) {
        viewModelScope.launch {
            firebaseAuth.value!!
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

                        phaseError.value = Pair(
                            PHASE.SIGNIN_EMAIL,
                            message
                        )
                    }
                }
        }
    }

    fun signUpWithEmail(email: String, pw: String) {
        viewModelScope.launch {
            firebaseAuth.value!!.createUserWithEmailAndPassword(email, pw)
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

                        phaseError.value = Pair(
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
                firebaseUserToken.value!!,
                fcmToken.value!!,
                firebaseAuth.value!!.uid!!,
                nickname
            ).collect {
                if (it.code() == 403) {
                    phaseError.value = Pair(
                        PHASE.SIGNUP_NICKNAME,
                        "이미 존재하는 닉네임입니다."
                    )
                } else {
                    val bundle = Bundle().apply {
                        putString(
                            FirebaseAnalytics.Param.ITEM_ID,
                            Constants.ANALYTICS.ID.SIGNUP
                        )
                        putString(
                            FirebaseAnalytics.Param.ITEM_NAME,
                            Constants.ANALYTICS.NAME.SIGNUP
                        )
                    }
                    firebaseAnalytics.value!!.logEvent(FirebaseAnalytics.Event.SIGN_UP, bundle)
                    phaseAllComplete.value = it.body()!!
                }
            }
        }
    }

    fun getFirebaseUserToken() {
        firebaseAuth.value!!.currentUser!!.getIdToken(true)
            .addOnCompleteListener { task: Task<GetTokenResult> ->
                if (task.isSuccessful) {
                    firebaseUserToken.value = task.result!!.token!!
                    checkUserInfo()
                } else {
                    task.exception?.printStackTrace()

                    val message = "토큰을 얻어오는데 실패했습니다."
                    phaseError.value = Pair(
                        PHASE.ACQUIRE_TOKEN,
                        message
                    )
                }
            }
    }

    fun checkUserInfo() {
        viewModelScope.launch {
            accountRepository.checkUser(
                firebaseUserToken.value!!,
                firebaseAuth.value!!.uid!!,
                fcmToken.value!!
            ).collect {
                if (it.code() == 409) {
                    App.showToast("닉네임을 입력해주세요.")

                    val message = "닉네임을 입력해주세요."
                    phaseError.value = Pair(
                        PHASE.CHECK_USERINFO,
                        message
                    )
                } else
                    phaseAllComplete.value = it.body()!!
            }
        }
    }
}

class AccountViewModelFactory(
    private val accountRepository: AccountRepository
) :
    ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(AccountViewModel::class.java)) {
            return AccountViewModel(
                accountRepository
            ) as T
        }
        throw IllegalArgumentException("Unknown View Model class")
    }
}