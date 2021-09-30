package com.yhjoo.dochef.ui.account

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
import com.google.firebase.auth.FirebaseAuth
import com.yhjoo.dochef.R
import com.yhjoo.dochef.data.repository.AccountRepository
import com.yhjoo.dochef.databinding.AccountSignupFragmentBinding
import com.yhjoo.dochef.ui.base.BaseActivity
import com.yhjoo.dochef.utils.ValidateUtil

class AccountSignUpFragment : Fragment() {
    private lateinit var binding: AccountSignupFragmentBinding
    private val accountViewModel: AccountViewModel by activityViewModels {
        AccountViewModelFactory(
            AccountRepository(requireContext().applicationContext)
        )
    }

    private lateinit var firebaseAuth: FirebaseAuth

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding =
            DataBindingUtil.inflate(inflater, R.layout.account_signup_fragment, container, false)
        val view: View = binding.root

        firebaseAuth = FirebaseAuth.getInstance()

        binding.apply {
            lifecycleOwner = viewLifecycleOwner

            signupEmailEdittext.apply {
                textChanged {
                    signupEmailLayout.error = null
                }
                setOnEditorActionListener { _, actionId, _ ->
                    if (actionId == EditorInfo.IME_ACTION_DONE) {
                        if (ValidateUtil.emailValidate(signupEmailEdittext.text.toString()) == ValidateUtil.EmailResult.ERR_INVALID) {
                            signupEmailLayout.error = "이메일 형식이 올바르지 않습니다."
                        } else {
                            signupEmailLayout.error = null
                            (requireActivity() as BaseActivity).hideKeyboard(signupEmailLayout)
                        }
                        true
                    } else
                        false
                }
            }

            signupPasswordEdittext.apply {
                textChanged {
                    signupPasswordLayout.error = null
                }
                setOnEditorActionListener { _, actionId, _ ->
                    if (actionId == EditorInfo.IME_ACTION_DONE) {
                        when {
                            ValidateUtil.pwValidate(signupPasswordEdittext.text.toString()) == ValidateUtil.PwResult.ERR_LENGTH ->
                                signupPasswordLayout.error =
                                    "비밀번호 길이를 확인 해 주세요. 8자 이상, 16자 이하로 입력 해 주세요."
                            ValidateUtil.pwValidate(signupPasswordEdittext.text.toString()) == ValidateUtil.PwResult.ERR_INVALID ->
                                signupPasswordLayout.error =
                                    "비밀번호 형식을 확인 해 주세요. 영문 및 숫자를 포함해야 합니다."
                            else -> {
                                signupPasswordLayout.error = null
                                (requireActivity() as BaseActivity).hideKeyboard(
                                    signupPasswordLayout
                                )
                            }
                        }
                        true
                    } else
                        false
                }

                signupOk.setOnClickListener { startSignUp() }
            }

            accountViewModel.phaseError.observe(viewLifecycleOwner, {
                if (it.first == AccountViewModel.CONSTANTS.PHASE.CHECK_USERINFO)
                    findNavController().navigate(
                        R.id.action_accountSignUpFragment_to_accountSignUpNickFragment
                    )
            })
        }

        return view
    }

    private fun startSignUp() {
        val email = binding.signupEmailEdittext.text.toString()
        val pw = binding.signupPasswordEdittext.text.toString()

        when {
            ValidateUtil.emailValidate(email) == ValidateUtil.EmailResult.ERR_INVALID -> {
                binding.signupEmailLayout.apply {
                    error = "이메일 형식이 올바르지 않습니다."
                    requestFocus()
                }
            }
            ValidateUtil.pwValidate(pw) == ValidateUtil.PwResult.ERR_LENGTH -> {
                binding.signupPasswordLayout.apply {
                    error = "비밀번호 길이를 확인 해 주세요. 8자 이상, 16자 이하로 입력 해 주세요."
                    requestFocus()
                }
            }
            ValidateUtil.pwValidate(pw) == ValidateUtil.PwResult.ERR_INVALID -> {
                binding.signupPasswordLayout.apply {
                    error = "비밀번호 형식을 확인 해 주세요. 영문 및 숫자를 포함해야 합니다."
                    requestFocus()
                }
            }
            else -> {
                (requireActivity() as BaseActivity).progressON(requireActivity())
                accountViewModel.signUpWithEmail(email, pw)
            }
        }
    }
}