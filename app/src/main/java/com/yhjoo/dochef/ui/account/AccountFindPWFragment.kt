package com.yhjoo.dochef.ui.account

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import androidx.fragment.app.Fragment
import com.afollestad.materialdialogs.utils.MDUtil.textChanged
import com.yhjoo.dochef.databinding.AccountFindpwFragmentBinding
import com.yhjoo.dochef.ui.base.BaseActivity
import com.yhjoo.dochef.utils.OtherUtil
import com.yhjoo.dochef.utils.ValidateUtil

class AccountFindPWFragment : Fragment() {
    private lateinit var binding: AccountFindpwFragmentBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = AccountFindpwFragmentBinding.inflate(inflater, container, false)
        val view: View = binding.root

        binding.apply {
            accountFindpwEdittext.apply {
                textChanged {
                    accountFindpwEmailLayout.error = null
                }
                setOnEditorActionListener { _, actionId, _ ->
                    if (actionId == EditorInfo.IME_ACTION_DONE) {
                        if (ValidateUtil.emailValidation(accountFindpwEdittext.text.toString()) == ValidateUtil.EmailValidate.INVALID) {
                            accountFindpwEmailLayout.error = "이메일 형식이 올바르지 않습니다."
                        } else {
                            accountFindpwEmailLayout.error = null
                            (requireActivity() as BaseActivity).hideKeyboard(
                                accountFindpwEmailLayout
                            )
                            findpw()
                        }
                        true
                    } else
                        false
                }
            }
        }

        return view
    }

    private fun findpw() {}
}