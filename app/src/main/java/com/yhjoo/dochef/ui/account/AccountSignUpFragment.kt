package com.yhjoo.dochef.ui.account

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.TextView
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.yhjoo.dochef.R
import com.yhjoo.dochef.data.repository.AccountRepository
import com.yhjoo.dochef.databinding.AccountSignupFragmentBinding
import com.yhjoo.dochef.ui.base.BaseActivity
import com.yhjoo.dochef.ui.base.BaseFragment
import com.yhjoo.dochef.utils.ValidateUtil
import kotlinx.coroutines.flow.collect

class AccountSignUpFragment : BaseFragment() {
    // TODO
    // BindingAdapter onEditorActionListener

    private lateinit var binding: AccountSignupFragmentBinding
    private val accountViewModel: AccountViewModel by activityViewModels {
        AccountViewModelFactory(
            AccountRepository(requireContext().applicationContext),
            requireActivity().application
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
            viewModel = accountViewModel

            signupEmailEdittext.setOnEditorActionListener(emailListener)
            signupPasswordEdittext.setOnEditorActionListener(pwListener)
        }

        subscribeEventOnLifecycle {
            accountViewModel.eventResult.collect {
                when (it.first) {
                    AccountViewModel.Events.SignUpEmail.ERROR_EMAIL -> {
                        binding.signupEmailLayout.run {
                            error = it.second
                            requestFocus()
                        }
                    }
                    AccountViewModel.Events.SignUpEmail.ERROR_PW -> {
                        binding.signupPasswordLayout.run {
                            error = it.second
                            requestFocus()
                        }
                    }
                    AccountViewModel.Events.SignUpEmail.WAIT -> {
                        binding.signupEmailLayout.error = null
                        binding.signupPasswordLayout.error = null
                        hideKeyboard(requireContext(), binding.signupPasswordLayout)
                        showProgress(requireActivity())
                    }
                    AccountViewModel.Events.SignUpEmail.ERROR_AUTH,
                    AccountViewModel.Events.Error.ERROR_REQUIRE_TOKEN -> {
                        hideProgress()
                        showSnackBar(binding.root, it.second!!)
                    }
                    AccountViewModel.Events.Error.ERROR_REQUIRE_SIGNUP -> {
                        hideProgress()
                        findNavController().navigate(
                            R.id.action_accountSignUpFragment_to_accountSignUpNickFragment
                        )
                    }
                }
            }
        }

        return binding.root
    }

    private val emailListener: TextView.OnEditorActionListener =
        TextView.OnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                val validateResult = ValidateUtil.emailValidate(
                    binding.signupEmailEdittext.text.toString()
                )

                if (validateResult.first == ValidateUtil.EmailResult.VALID) {
                    binding.signupEmailLayout.error = null
                    (requireActivity() as BaseActivity).hideKeyboard(binding.signupEmailLayout)
                } else {
                    binding.signupEmailLayout.error = validateResult.second
                }

                true
            } else
                false
        }

    private val pwListener: TextView.OnEditorActionListener =
        TextView.OnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                val validateResult = ValidateUtil.pwValidate(
                    binding.signupPasswordEdittext.text.toString()
                )

                if (validateResult.first == ValidateUtil.PwResult.VALID) {
                    binding.signupPasswordLayout.error = null
                    (requireActivity() as BaseActivity).hideKeyboard(binding.signupPasswordLayout)
                } else {
                    binding.signupPasswordLayout.error = validateResult.second
                }

                true
            } else
                false
        }
}