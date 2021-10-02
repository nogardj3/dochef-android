package com.yhjoo.dochef.ui.account

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.TextView
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.afollestad.materialdialogs.utils.MDUtil.textChanged
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.yhjoo.dochef.Constants
import com.yhjoo.dochef.R
import com.yhjoo.dochef.data.repository.AccountRepository
import com.yhjoo.dochef.databinding.AccountSigninFragmentBinding
import com.yhjoo.dochef.ui.base.BaseActivity
import com.yhjoo.dochef.utils.ValidateUtil

class AccountSignInFragment : Fragment() {
    // TODO
    // Google Signin
    private lateinit var binding: AccountSigninFragmentBinding
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
            DataBindingUtil.inflate(inflater, R.layout.account_signin_fragment, container, false)

        binding.apply {
            lifecycleOwner = viewLifecycleOwner

            signinEmailEdittext.apply {
                textChanged {
                    signinEmailLayout.error = null
                }
                setOnEditorActionListener(emailListener)
            }
            signinPasswordEdittext.apply {
                textChanged {
                    signinPasswordEdittext.error = null
                }
                setOnEditorActionListener (pwListener)
            }

            signinOk.setOnClickListener { signInWithEmail() }
            signinGoogle.setOnClickListener {
                startActivityForResult(
                    accountViewModel.googleSigninIntent,
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

        return binding.root
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == Constants.GOOGLE_SIGNIN_CODE) {
            try {
                (requireActivity() as BaseActivity).showProgress(requireActivity())
                val result = GoogleSignIn.getSignedInAccountFromIntent(data).result.idToken!!
                accountViewModel.signInWithGoogle(result)
            } catch (e: Exception) {
                e.printStackTrace()
                (requireActivity() as BaseActivity).showSnackBar(
                    binding.root,
                    "구글 인증 오류. 잠시 후 다시 시도해주세요."
                )
                (requireActivity() as BaseActivity).hideProgress()
            }
        }
    }

    private val emailListener: TextView.OnEditorActionListener =
        TextView.OnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                val validateResult = ValidateUtil.emailValidate(
                    binding.signinEmailEdittext.text.toString()
                )

                if (validateResult.first == ValidateUtil.EmailResult.VALID) {
                    binding.signinEmailLayout.error = null
                    (requireActivity() as BaseActivity).hideKeyboard(binding.signinEmailLayout)
                } else
                    binding.signinEmailLayout.error = validateResult.second
                true
            } else
                false
        }

    private val pwListener: TextView.OnEditorActionListener =
        TextView.OnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                val validateResult = ValidateUtil.pwValidate(
                    binding.signinPasswordEdittext.text.toString()
                )

                if (validateResult.first == ValidateUtil.PwResult.VALID) {
                    binding.signinPasswordLayout.error = null
                    (requireActivity() as BaseActivity).hideKeyboard(binding.signinPasswordLayout)
                } else
                    binding.signinPasswordLayout.error = validateResult.second
                true
            } else
                false
        }

    private fun signInWithEmail() {
        val signinEmail = binding.signinEmailEdittext.text.toString()
        val signinPw = binding.signinPasswordEdittext.text.toString()

        val emailValidateResult = ValidateUtil.emailValidate(signinEmail)
        val pwValidateResult = ValidateUtil.pwValidate(signinPw)

        when {
            emailValidateResult.first != ValidateUtil.EmailResult.VALID -> {
                binding.signinEmailLayout.apply {
                    error = emailValidateResult.second
                    requestFocus()
                }
            }
            pwValidateResult.first != ValidateUtil.PwResult.VALID -> {
                binding.signinPasswordLayout.apply {
                    error = pwValidateResult.second
                    requestFocus()
                }
            }
            else -> {
                binding.signinEmailLayout.error = null
                binding.signinPasswordLayout.error = null
                (requireActivity() as BaseActivity).hideKeyboard(binding.signinPasswordLayout)
                (requireActivity() as BaseActivity).showProgress(requireActivity())
                accountViewModel.signInWithEmail(signinEmail, signinPw)
            }
        }
    }
}