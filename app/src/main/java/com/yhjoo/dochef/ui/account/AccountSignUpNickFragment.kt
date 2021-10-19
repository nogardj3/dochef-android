package com.yhjoo.dochef.ui.account

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.TextView
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.activityViewModels
import com.yhjoo.dochef.R
import com.yhjoo.dochef.data.repository.AccountRepository
import com.yhjoo.dochef.databinding.AccountSignupnickFragmentBinding
import com.yhjoo.dochef.ui.base.BaseActivity
import com.yhjoo.dochef.ui.base.BaseFragment
import com.yhjoo.dochef.utils.ValidateUtil
import kotlinx.coroutines.flow.collect

class AccountSignUpNickFragment : BaseFragment() {
    // TODO
    // BindingAdapter onEditorActionListener

    private lateinit var binding: AccountSignupnickFragmentBinding
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
        binding = DataBindingUtil.inflate(
            inflater,
            R.layout.account_signupnick_fragment,
            container,
            false
        )

        binding.apply {
            lifecycleOwner = viewLifecycleOwner
            viewModel = accountViewModel

            signupnickNicknameEdittext.setOnEditorActionListener(nicknameListener)
        }

        subscribeEventOnLifecycle {
            accountViewModel.eventResult.collect {
                when (it.first) {
                    AccountViewModel.Events.SignUpNickname.ERROR -> {
                        binding.signupnickNicknameLayout.apply {
                            error = it.second
                            requestFocus()
                        }
                    }
                    AccountViewModel.Events.SignUpNickname.WAIT -> {
                        binding.signupnickNicknameLayout.error = null
                        hideKeyboard(requireContext(), binding.signupnickNicknameLayout)
                        showProgress(requireActivity())
                    }
                }
            }
        }

        return binding.root
    }

    private val nicknameListener: TextView.OnEditorActionListener =
        TextView.OnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                val validateResult = ValidateUtil.nicknameValidate(
                    binding.signupnickNicknameEdittext.text.toString()
                )

                if (validateResult.first == ValidateUtil.NicknameResult.VALID) {
                    binding.signupnickNicknameLayout.error = null
                    (requireActivity() as BaseActivity).hideKeyboard(
                        binding.signupnickNicknameLayout
                    )
                } else {
                    binding.signupnickNicknameLayout.error = validateResult.second
                }

                true
            } else false
        }
}