package com.yhjoo.dochef.ui.account

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.afollestad.materialdialogs.utils.MDUtil.textChanged
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.yhjoo.dochef.App
import com.yhjoo.dochef.Constants
import com.yhjoo.dochef.R
import com.yhjoo.dochef.data.repository.AccountRepository
import com.yhjoo.dochef.databinding.AccountSigninFragmentBinding
import com.yhjoo.dochef.ui.base.BaseActivity
import com.yhjoo.dochef.utils.ValidateUtil

class AccountSignInFragment : Fragment() {
    private lateinit var binding: AccountSigninFragmentBinding
    private val accountViewModel: AccountViewModel by activityViewModels {
        AccountViewModelFactory(
            AccountRepository(requireContext().applicationContext)
        )
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding =
            DataBindingUtil.inflate(inflater, R.layout.account_signin_fragment, container, false)
        val view: View = binding.root

        binding.apply {
            lifecycleOwner = viewLifecycleOwner

            signinEmailEdittext.apply {
                textChanged {
                    signinEmailLayout.error = null
                }
                setOnEditorActionListener { _, actionId, _ ->
                    if (actionId == EditorInfo.IME_ACTION_DONE) {
                        if (ValidateUtil.emailValidate(text.toString()) == ValidateUtil.EmailResult.ERR_INVALID) {
                            signinEmailLayout.error = "이메일 형식이 올바르지 않습니다."
                        } else {
                            signinEmailLayout.error = null
                            (requireActivity() as BaseActivity).hideKeyboard(signinEmailLayout)
                        }
                        true
                    } else
                        false
                }
            }
            signinPasswordEdittext.apply {
                textChanged {
                    signinPasswordEdittext.error = null
                }
                setOnEditorActionListener { _, actionId, _ ->
                    if (actionId == EditorInfo.IME_ACTION_DONE) {
                        when {
                            ValidateUtil.pwValidate(signinPasswordEdittext.text.toString()) == ValidateUtil.PwResult.ERR_LENGTH ->
                                signinPasswordLayout.error =
                                    "비밀번호 길이를 확인 해 주세요. 8자 이상, 16자 이하로 입력 해 주세요."
                            ValidateUtil.pwValidate(signinPasswordEdittext.text.toString()) == ValidateUtil.PwResult.ERR_INVALID ->
                                signinPasswordLayout.error =
                                    "비밀번호 형식을 확인 해 주세요. 영문 및 숫자를 포함해야 합니다."
                            else -> {
                                signinPasswordLayout.error = null
                                (requireActivity() as BaseActivity).hideKeyboard(
                                    signinPasswordLayout
                                )
                            }
                        }
                        true
                    } else
                        false
                }
            }

            signinOk.setOnClickListener { signInWithEmail() }
            signinGoogle.setOnClickListener {
                startActivityForResult(
                    accountViewModel.googleClient.value!!.signInIntent,
                    Constants.GOOGLE_SIGNIN_CODE
                )
            }
            signinSignupBtn.setOnClickListener {
                findNavController().navigate(R.id.action_accountSignInFragment_to_accountSignUpFragment)
            }
            signinFindpwBtn.setOnClickListener {
                findNavController().navigate(R.id.action_accountSignInFragment_to_accountFindPWFragment)
            }
            accountViewModel.phaseError.observe(viewLifecycleOwner, {
                if (it.first == AccountViewModel.CONSTANTS.PHASE.CHECK_USERINFO)
                    findNavController().navigate(
                        R.id.action_accountSignInFragment_to_accountSignUpNickFragment
                    )
            })
        }

        return view
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == Constants.GOOGLE_SIGNIN_CODE) {
            try {
                (requireActivity() as BaseActivity).progressON(requireActivity())
                val result = GoogleSignIn.getSignedInAccountFromIntent(data).result.idToken!!
                accountViewModel.signInWithGoogle(requireActivity(), result)
            } catch (e: Exception) {
                e.printStackTrace()
                App.showToast("구글 인증 오류. 잠시 후 다시 시도해주세요.")
                (requireActivity() as BaseActivity).progressOFF()
            }
        }
    }

    private fun signInWithEmail() {
        val signinEmail = binding.signinEmailEdittext.text.toString()
        val signinPw = binding.signinPasswordEdittext.text.toString()
        when {
            ValidateUtil.emailValidate(signinEmail) == ValidateUtil.EmailResult.ERR_INVALID -> {
                binding.signinEmailLayout.apply {
                    error = "이메일 형식이 올바르지 않습니다."
                    requestFocus()
                }
            }
            ValidateUtil.pwValidate(signinPw) == ValidateUtil.PwResult.ERR_LENGTH -> {
                binding.signinPasswordLayout.apply {
                    error = "비밀번호 길이를 확인 해 주세요. 8자 이상, 16자 이하로 입력 해 주세요."
                    requestFocus()
                }
            }
            ValidateUtil.pwValidate(signinPw) == ValidateUtil.PwResult.ERR_INVALID -> {
                binding.signinPasswordLayout.apply {
                    error = "비밀번호 형식을 확인 해 주세요. 영문 및 숫자를 포함해야 합니다."
                    requestFocus()
                }
            }
            else -> {
                (requireActivity() as BaseActivity).progressON(requireActivity())
                accountViewModel.signInWithEmail(signinEmail, signinPw)
            }
        }
    }
}