package com.yhjoo.dochef.ui.account

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.afollestad.materialdialogs.utils.MDUtil.textChanged
import com.yhjoo.dochef.R
import com.yhjoo.dochef.data.repository.AccountRepository
import com.yhjoo.dochef.databinding.AccountSignupnickFragmentBinding
import com.yhjoo.dochef.ui.base.BaseActivity
import com.yhjoo.dochef.utils.ValidateUtil

class AccountSignUpNickFragment : Fragment() {
    private lateinit var binding: AccountSignupnickFragmentBinding
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
        binding = DataBindingUtil.inflate(
            inflater,
            R.layout.account_signupnick_fragment,
            container,
            false
        )
        val view: View = binding.root

        binding.apply {
            lifecycleOwner = viewLifecycleOwner

            signupnickNicknameEdittext.apply {
                textChanged {
                    signupnickNicknameLayout.error = null
                }
                setOnEditorActionListener { _, actionId, _ ->
                    if (actionId == EditorInfo.IME_ACTION_DONE) {
                        when {
                            ValidateUtil.pwValidate(signupnickNicknameEdittext.text.toString()) == ValidateUtil.PwResult.ERR_LENGTH ->
                                signupnickNicknameLayout.error =
                                    "닉네임 길이를 확인 해 주세요. 6자 이상, 10자 이하로 입력해주세요."
                            ValidateUtil.pwValidate(signupnickNicknameEdittext.text.toString()) == ValidateUtil.PwResult.ERR_INVALID ->
                                signupnickNicknameLayout.error =
                                    "닉네임 형식을 확인 해 주세요. 숫자, 알파벳 대소문자, 한글만 사용가능합니다."
                            else -> {
                                signupnickNicknameLayout.error = null
                                (requireActivity() as BaseActivity).hideKeyboard(
                                    signupnickNicknameLayout
                                )
                            }
                        }
                        true
                    } else
                        false
                }
            }

            signupnickOk.setOnClickListener { signUpWithEmail() }
        }

        return view
    }

    private fun signUpWithEmail() {
        val nickname = binding.signupnickNicknameEdittext.text.toString()

        when {
            ValidateUtil.nicknameValidate(nickname) == ValidateUtil.PwResult.ERR_LENGTH ->
                binding.signupnickNicknameLayout.apply {
                    error = "닉네임 길이를 확인 해 주세요. 6자 이상, 10자 이하로 입력해주세요."
                    requestFocus()
                }
            ValidateUtil.nicknameValidate(nickname) == ValidateUtil.PwResult.ERR_INVALID ->
                binding.signupnickNicknameLayout.apply {
                    error = "닉네임 형식을 확인 해 주세요. 숫자, 알파벳 대소문자, 한글만 사용가능합니다."
                    requestFocus()
                }
            else -> {
                (requireActivity() as BaseActivity).progressON(requireActivity())
                accountViewModel.signUpWithNickname(nickname)
            }
        }
    }
}