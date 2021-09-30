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
import com.yhjoo.dochef.databinding.AccountFindpwFragmentBinding
import com.yhjoo.dochef.ui.base.BaseActivity
import com.yhjoo.dochef.utils.ValidateUtil

class AccountFindPWFragment : Fragment() {
    private lateinit var binding: AccountFindpwFragmentBinding
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
            DataBindingUtil.inflate(inflater, R.layout.account_findpw_fragment, container, false)

        binding.apply {
            lifecycleOwner = viewLifecycleOwner

            accountFindpwEdittext.apply {
                textChanged {
                    accountFindpwEmailLayout.error = null
                }
                setOnEditorActionListener { _, actionId, _ ->
                    if (actionId == EditorInfo.IME_ACTION_DONE) {
                        val validateResult = ValidateUtil.emailValidate(
                            accountFindpwEdittext.text.toString()
                        )

                        if (validateResult.first == ValidateUtil.EmailResult.VALID) {
                            accountFindpwEmailLayout.error = null
                            (requireActivity() as BaseActivity).hideKeyboard(
                                accountFindpwEmailLayout
                            )
                        } else
                            accountFindpwEmailLayout.error = validateResult.second
                        true
                    } else
                        false
                }
            }

            accountFindpwOk.setOnClickListener {
                findPW(accountFindpwEdittext.text.toString())
            }
        }

        return binding.root
    }

    private fun findPW(email: String) {
        val validateResult = ValidateUtil.emailValidate(email)

        if (validateResult.first == ValidateUtil.EmailResult.VALID) {
            binding.accountFindpwEmailLayout.error = null
            (requireActivity() as BaseActivity).hideKeyboard(binding.accountFindpwEmailLayout)
            (requireActivity() as BaseActivity).showProgress(requireActivity())
            accountViewModel.sendPasswordResetEmail(email)
        } else
            binding.accountFindpwEmailLayout.apply{
                error = validateResult.second
                requestFocus()
            }
    }
}