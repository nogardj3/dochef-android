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
import com.yhjoo.dochef.databinding.AccountFindpwFragmentBinding
import com.yhjoo.dochef.ui.base.BaseFragment
import com.yhjoo.dochef.utils.ValidateUtil
import kotlinx.coroutines.flow.collect

class AccountFindPWFragment : BaseFragment() {
    // TODO
    // BindingAdapter onEditorActionListener

    private lateinit var binding: AccountFindpwFragmentBinding
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
            DataBindingUtil.inflate(inflater, R.layout.account_findpw_fragment, container, false)

        binding.apply {
            lifecycleOwner = viewLifecycleOwner
            viewModel = accountViewModel

            accountFindpwEdittext.setOnEditorActionListener(emailListener)
        }

        subscribeEventOnLifecycle {
            accountViewModel.eventResult.collect {
                when (it.first) {
                    AccountViewModel.Events.FindPW.ERROR_EMAIL -> {
                        binding.accountFindpwEmailLayout.apply {
                            error = it.second
                            requestFocus()
                        }
                    }
                    AccountViewModel.Events.FindPW.WAIT -> {
                        binding.accountFindpwEmailLayout.error = null
                        hideKeyboard(requireContext(), binding.accountFindpwEmailLayout)
                        showProgress(requireActivity())
                    }
                    AccountViewModel.Events.FindPW.COMPLETE -> {
                        hideProgress()
                        showSnackBar(binding.root, "메일을 전송했습니다. 메일을 확인 해 주세요.")
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
                    binding.accountFindpwEdittext.text.toString()
                )

                if (validateResult.first == ValidateUtil.EmailResult.VALID) {
                    binding.accountFindpwEmailLayout.error = null
                    hideKeyboard(requireContext(), binding.accountFindpwEmailLayout)
                } else
                    binding.accountFindpwEmailLayout.error = validateResult.second
                true
            } else false
        }
}