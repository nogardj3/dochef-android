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
            requireActivity().application,
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

        binding.apply {
            lifecycleOwner = viewLifecycleOwner

            signupnickNicknameEdittext.apply {
                textChanged {
                    signupnickNicknameLayout.error = null
                }
                setOnEditorActionListener { _, actionId, _ ->
                    if (actionId == EditorInfo.IME_ACTION_DONE) {
                        val validateResult = ValidateUtil.nicknameValidate(
                            signupnickNicknameEdittext.text.toString()
                        )

                        if (validateResult.first == ValidateUtil.NicknameResult.VALID) {
                            signupnickNicknameLayout.error = null
                            (requireActivity() as BaseActivity).hideKeyboard(
                                signupnickNicknameLayout
                            )
                        } else
                            signupnickNicknameLayout.error = validateResult.second

                        true
                    } else
                        false
                }
            }

            signupnickOk.setOnClickListener { signUpWithEmail(signupnickNicknameEdittext.text.toString()) }
        }

        return binding.root
    }

    private fun signUpWithEmail(nickname: String) {
        val validateResult = ValidateUtil.nicknameValidate(nickname)

        if (validateResult.first == ValidateUtil.NicknameResult.VALID) {
            binding.signupnickNicknameLayout.error = null
            (requireActivity() as BaseActivity).hideKeyboard(binding.signupnickNicknameLayout)
            (requireActivity() as BaseActivity).showProgress(requireActivity())
            accountViewModel.signUpWithNickname(nickname)
        } else
            binding.signupnickNicknameLayout.apply {
                error = validateResult.second
                requestFocus()
            }
    }
}