package com.yhjoo.dochef.ui.fragments

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.*
import com.google.firebase.messaging.FirebaseMessaging
import com.yhjoo.dochef.App
import com.yhjoo.dochef.R
import com.yhjoo.dochef.model.*
import com.yhjoo.dochef.databinding.*
import com.yhjoo.dochef.ui.activities.AccountActivity
import com.yhjoo.dochef.ui.activities.BaseActivity
import com.yhjoo.dochef.utils.RetrofitBuilder
import com.yhjoo.dochef.utils.RetrofitServices
import com.yhjoo.dochef.utils.Utils

class AccountSignInFragment : Fragment() {
    object CODE {
        const val RC_SIGN_IN = 9001
    }

    private lateinit var binding: AccountSigninFragmentBinding
    private lateinit var accountService: RetrofitServices.AccountService
    private lateinit var googleSignInClient: GoogleSignInClient
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var idToken: String
    private lateinit var fcmToken: String

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = AccountSigninFragmentBinding.inflate(inflater, container, false)
        val view: View = binding.root

        firebaseAuth = FirebaseAuth.getInstance()
        googleSignInClient = GoogleSignIn.getClient(
            requireActivity(),
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
        accountService =
            RetrofitBuilder.create(requireContext(), RetrofitServices.AccountService::class.java)

        binding.apply {
            accountSigninOk.setOnClickListener { signInWithEmailPW() }
            accountSigninGoogle.setOnClickListener { startActivityForResult(googleSignInClient.signInIntent, CODE.RC_SIGN_IN) }
            accountSigninSignup.setOnClickListener { findNavController().navigate(R.id.action_accountSignInFragment_to_accountSignUpFragment) }
            accountSigninFindpw.setOnClickListener { findNavController().navigate(R.id.action_accountSignInFragment_to_accountFindPWFragment) }
        }

        return view
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == CODE.RC_SIGN_IN) {
            (requireActivity() as BaseActivity).progressON(requireActivity())
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)

            try {
                val account = task.getResult(ApiException::class.java)
                Utils.log("firebaseAuthWithGoogle:" + account!!.id)
                signInWithGoogle(account.idToken!!)
            } catch (e: ApiException) {
                Utils.log("Google sign in failed", e.toString())
                App.showToast("구글 인증 오류. 잠시 후 다시 시도해주세요.")
                (requireActivity() as BaseActivity).progressOFF()
            }
        }
    }

    private fun signInWithEmailPW() {
        val signinEmail = binding.accountSigninEmail.text.toString()
        val signinPw = binding.accountSigninPassword.text.toString()

        when {
            Utils.emailValidation(signinEmail) == Utils.EmailValidate.NODATA || Utils.pwValidation(
                signinPw
            ) == Utils.PWValidate.NODATA ->
                App.showToast("이메일과 비밀번호를 모두 입력해주세요.")
            Utils.emailValidation(signinEmail) == Utils.EmailValidate.INVALID ->
                App.showToast("이메일 형식이 올바르지 않습니다.")
            Utils.pwValidation(signinPw) == Utils.PWValidate.LENGTH ->
                App.showToast("비밀번호 길이를 확인 해 주세요. 8자 이상, 16자 이하로 입력 해 주세요.")
            Utils.pwValidation(signinPw) == Utils.PWValidate.INVALID ->
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
                            (requireActivity() as BaseActivity).progressOFF()
                        } else {
                            authTask.result!!.user!!.getIdToken(true)
                                .addOnCompleteListener { task: Task<GetTokenResult> ->
                                    if (!task.isSuccessful) {
                                        (requireActivity() as BaseActivity).progressOFF()
                                        App.showToast("알 수 없는 오류 발생. 다시 시도해 주세요")
                                    } else {
                                        idToken = task.result!!.token!!
                                        (requireActivity() as AccountActivity).checkUserInfo(
                                            idToken,R.id.action_accountSignInFragment_to_accountSignUpNickFragment)
                                    }
                                }
                        }
                    }
            }
        }
    }

    private fun signInWithGoogle(googleToken: String) {
        val credential = GoogleAuthProvider.getCredential(googleToken, null)

        firebaseAuth.signInWithCredential(credential)
            .addOnCompleteListener(requireActivity()) { task: Task<AuthResult?> ->
                if (task.isSuccessful) {
                    val user = firebaseAuth.currentUser
                    user!!.getIdToken(true)
                        .addOnCompleteListener { tokenTask: Task<GetTokenResult> ->
                            if (!tokenTask.isSuccessful) {
                                (requireActivity() as BaseActivity).progressOFF()
                                App.showToast("알 수 없는 오류가 발생. 다시 시도해 주세요")
                            } else {
                                idToken = tokenTask.result!!.token!!
                                (requireActivity() as AccountActivity).checkUserInfo(
                                    idToken,R.id.action_accountSignInFragment_to_accountSignUpNickFragment)
                            }
                        }
                } else {
                    task.exception?.printStackTrace()
                    App.showToast("알 수 없는 오류 발생. 다시 시도해 주세요.")
                }
            }
    }
}