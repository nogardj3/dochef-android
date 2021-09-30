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
import com.yhjoo.dochef.R
import com.yhjoo.dochef.data.repository.AccountRepository
import com.yhjoo.dochef.databinding.AccountSignupFragmentBinding
import com.yhjoo.dochef.ui.base.BaseActivity
import com.yhjoo.dochef.utils.ValidateUtil

class AccountSignUpFragment : Fragment() {
    private lateinit var binding: AccountSignupFragmentBinding
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
        binding =
            DataBindingUtil.inflate(inflater, R.layout.account_signup_fragment, container, false)

        binding.apply {
            lifecycleOwner = viewLifecycleOwner

            signupEmailEdittext.apply {
                textChanged {
                    signupEmailLayout.error = null
                }
                setOnEditorActionListener { _, actionId, _ ->
                    if (actionId == EditorInfo.IME_ACTION_DONE) {
                        val validateResult = ValidateUtil.emailValidate(
                            signupEmailEdittext.text.toString()
                        )

                        if (validateResult.first == ValidateUtil.EmailResult.VALID) {
                            signupEmailLayout.error = null
                            (requireActivity() as BaseActivity).hideKeyboard(signupEmailLayout)
                        } else
                            signupEmailLayout.error = validateResult.second

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
                        val validateResult = ValidateUtil.pwValidate(
                            signupPasswordEdittext.text.toString()
                        )

                        if (validateResult.first == ValidateUtil.PwResult.VALID) {
                            signupPasswordLayout.error = null
                            (requireActivity() as BaseActivity).hideKeyboard(signupPasswordLayout)
                        } else
                            signupPasswordLayout.error = validateResult.second

                        true
                    } else
                        false
                }

                signupOk.setOnClickListener {
                    startSignUp(
                        binding.signupEmailEdittext.text.toString(),
                        binding.signupPasswordEdittext.text.toString()
                    )
                }
            }

            accountViewModel.phaseError.observe(viewLifecycleOwner, {
                if (it.first == AccountViewModel.CONSTANTS.PHASE.CHECK_USERINFO)
                    findNavController().navigate(
                        R.id.action_accountSignUpFragment_to_accountSignUpNickFragment
                    )
            })
        }

        return binding.root
    }

    private fun startSignUp(email: String, pw: String) {
        val emailValidateResult = ValidateUtil.emailValidate(email)
        val pwValidateResult = ValidateUtil.pwValidate(pw)

        when {
            emailValidateResult.first != ValidateUtil.EmailResult.VALID -> {
                binding.signupEmailLayout.apply {
                    error = emailValidateResult.second
                    requestFocus()
                }
            }
            pwValidateResult.first != ValidateUtil.PwResult.VALID -> {
                binding.signupPasswordLayout.apply {
                    error = pwValidateResult.second
                    requestFocus()
                }
            }
            else -> {
                binding.signupEmailLayout.error = null
                binding.signupPasswordLayout.error = null
                (requireActivity() as BaseActivity).hideKeyboard(binding.signupPasswordLayout)
                (requireActivity() as BaseActivity).showProgress(requireActivity())
                accountViewModel.signUpWithEmail(email, pw)
            }
        }
    }
}