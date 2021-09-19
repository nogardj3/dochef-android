package com.yhjoo.dochef.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import androidx.fragment.app.Fragment
import com.afollestad.materialdialogs.utils.MDUtil.textChanged
import com.google.android.gms.tasks.Task
import com.google.firebase.FirebaseNetworkException
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthException
import com.google.firebase.auth.GetTokenResult
import com.yhjoo.dochef.App
import com.yhjoo.dochef.R
import com.yhjoo.dochef.databinding.*
import com.yhjoo.dochef.ui.activities.AccountActivity
import com.yhjoo.dochef.ui.activities.BaseActivity
import com.yhjoo.dochef.utilities.Utils

class AccountSignUpFragment : Fragment() {
    private lateinit var binding: AccountSignupFragmentBinding
    private lateinit var firebaseAuth: FirebaseAuth

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = AccountSignupFragmentBinding.inflate(inflater, container, false)
        val view: View = binding.root

        firebaseAuth = FirebaseAuth.getInstance()

        binding.apply {
            accountSignupEmailEdittext.textChanged {
                binding.accountSignupEmailLayout.error = null
            }
            binding.accountSignupEmailEdittext.setOnEditorActionListener { v, actionId, event ->
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    if (Utils.emailValidation(binding.accountSignupEmailEdittext.text.toString()) == Utils.EmailValidate.INVALID) {
                        binding.accountSignupEmailLayout.error = "이메일 형식이 올바르지 않습니다."
                    } else {
                        binding.accountSignupEmailLayout.error = null
                        (requireActivity() as BaseActivity).hideKeyboard(binding.accountSignupEmailLayout)
                    }
                    true
                } else
                    false
            }

            accountSignupPasswordEdittext.textChanged {
                binding.accountSignupPasswordLayout.error = null
            }
            accountSignupPasswordEdittext.setOnEditorActionListener { v, actionId, event ->
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    if (Utils.pwValidation(binding.accountSignupPasswordEdittext.text.toString()) == Utils.PWValidate.LENGTH) {
                        binding.accountSignupPasswordLayout.error =
                            "비밀번호 길이를 확인 해 주세요. 8자 이상, 16자 이하로 입력 해 주세요."
                    } else if (Utils.pwValidation(binding.accountSignupPasswordEdittext.text.toString()) == Utils.PWValidate.INVALID) {
                        binding.accountSignupPasswordLayout.error =
                            "비밀번호 형식을 확인 해 주세요. 영문 및 숫자를 포함해야 합니다."
                    } else {
                        binding.accountSignupPasswordLayout.error = null
                        (requireActivity() as BaseActivity).hideKeyboard(binding.accountSignupPasswordLayout)
                    }
                    true
                } else
                    false
            }

            accountSignupOk.setOnClickListener { startSignUp() }
        }

        return view
    }

    private fun startSignUp() {
        val email = binding.accountSignupEmailEdittext.text.toString()
        val pw = binding.accountSignupPasswordEdittext.text.toString()

        if (Utils.emailValidation(email) != Utils.EmailValidate.VALID)
            binding.accountSignupEmailLayout.requestFocus()
        else if (Utils.pwValidation(pw) != Utils.PWValidate.VALID)
            binding.accountSignupPasswordEdittext.requestFocus()
        else
            (requireActivity() as BaseActivity).progressON(requireActivity())
        firebaseAuth.createUserWithEmailAndPassword(email, pw)
            .addOnCompleteListener { authTask: Task<AuthResult> ->
                if (!authTask.isSuccessful) {
                    (requireActivity() as BaseActivity).progressOFF()

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
                                (requireActivity() as BaseActivity).progressOFF()
                                App.showToast("알 수 없는 오류가 발생. 다시 시도해 주세요")
                            } else {
                                val idToken = task.result!!.token!!
                                (requireActivity() as AccountActivity).checkUserInfo(
                                    idToken,R.id.action_accountSignUpFragment_to_accountSignUpNickFragment)
                            }
                        }
                }
            }
    }
}