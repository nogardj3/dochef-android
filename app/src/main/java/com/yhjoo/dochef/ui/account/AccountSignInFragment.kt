package com.yhjoo.dochef.ui.account

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.TextView
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.afollestad.materialdialogs.utils.MDUtil.textChanged
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.yhjoo.dochef.Constants
import com.yhjoo.dochef.R
import com.yhjoo.dochef.data.repository.AccountRepository
import com.yhjoo.dochef.databinding.AccountSigninFragmentBinding
import com.yhjoo.dochef.ui.base.BaseActivity
import com.yhjoo.dochef.ui.base.BaseFragment
import com.yhjoo.dochef.utils.ValidateUtil
import kotlinx.coroutines.flow.collect

class AccountSignInFragment : BaseFragment() {
    // TODO
    // GoogleSignin + Viewmodel

    private lateinit var binding: AccountSigninFragmentBinding
    private val accountViewModel: AccountViewModel by activityViewModels {
        AccountViewModelFactory(
            requireActivity().application,
            AccountRepository(requireContext().applicationContext)
        )
    }

    enum class Navigate {
        FINDPW, SIGNUP, SIGNUPNICK
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
            viewModel = accountViewModel

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
                setOnEditorActionListener(pwListener)
            }

            signinGoogle.setOnClickListener {
                startActivityForResult(
                    accountViewModel.googleSigninIntent,
                    Constants.GOOGLE_SIGNIN_CODE
                )
            }

            signinSignupBtn.setOnClickListener {
                navigateFragment(Navigate.SIGNUP)
            }

            signinFindpwBtn.setOnClickListener {
                navigateFragment(Navigate.FINDPW)
            }
        }

        eventOnLifecycle {
            accountViewModel.eventResult.collect {
                when (it.first) {
                    AccountViewModel.Events.SignInEmail.ERROR_EMAIL -> {
                        binding.signinEmailLayout.run {
                            error = it.second
                            requestFocus()
                        }
                    }
                    AccountViewModel.Events.SignInEmail.ERROR_PW -> {
                        binding.signinPasswordLayout.run {
                            error = it.second
                            requestFocus()
                        }
                    }
                    AccountViewModel.Events.SignInEmail.WAIT -> {
                        binding.signinEmailLayout.error = null
                        binding.signinPasswordLayout.error = null
                        hideKeyboard(requireContext(), binding.signinPasswordLayout)
                        showProgress(requireActivity())
                    }
                    AccountViewModel.Events.SignInEmail.ERROR_AUTH,
                    AccountViewModel.Events.SignInGoogle.ERROR_GOOGLE,
                    AccountViewModel.Events.Error.ERROR_REQUIRE_TOKEN -> {
                        hideProgress()
                        showSnackBar(binding.root, it.second!!)
                    }
                    AccountViewModel.Events.Error.ERROR_REQUIRE_SIGNUP -> {
                        hideProgress()
                        navigateFragment(Navigate.SIGNUPNICK)
                    }
                }
            }
        }

        return binding.root
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == Constants.GOOGLE_SIGNIN_CODE) {
            try {
                showProgress(requireActivity())
                val result = GoogleSignIn.getSignedInAccountFromIntent(data).result.idToken!!
                accountViewModel.clickSignInWithGoogle(result)
            } catch (e: Exception) {
                e.printStackTrace()
                showSnackBar(
                    binding.root,
                    "구글 인증 오류. 잠시 후 다시 시도해주세요."
                )
                hideProgress()
            }
        }
    }

    private fun navigateFragment(target: Navigate) {
        val targetFragment = when (target) {
            Navigate.FINDPW -> R.id.action_accountSignInFragment_to_accountFindPWFragment
            Navigate.SIGNUP -> R.id.action_accountSignInFragment_to_accountSignUpFragment
            Navigate.SIGNUPNICK -> R.id.action_accountSignInFragment_to_accountSignUpNickFragment
        }

        findNavController().navigate(targetFragment)
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
            } else false
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
                } else {
                    binding.signinPasswordLayout.error = validateResult.second
                }
                true
            } else
                false
        }
}