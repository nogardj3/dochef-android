package com.yhjoo.dochef.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import androidx.fragment.app.Fragment
import com.afollestad.materialdialogs.utils.MDUtil.textChanged
import com.google.android.gms.tasks.Task
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.messaging.FirebaseMessaging
import com.yhjoo.dochef.App
import com.yhjoo.dochef.R
import com.yhjoo.dochef.databinding.*
import com.yhjoo.dochef.model.*
import com.yhjoo.dochef.ui.activities.AccountActivity
import com.yhjoo.dochef.ui.activities.BaseActivity
import com.yhjoo.dochef.utilities.RetrofitBuilder
import com.yhjoo.dochef.utilities.RetrofitServices
import com.yhjoo.dochef.utilities.Utils
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class AccountSignUpNickFragment : Fragment() {
    private lateinit var binding: AccountSignupnickFragmentBinding
    private lateinit var accountService: RetrofitServices.AccountService
    private lateinit var firebaseAnalytics: FirebaseAnalytics
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var idToken: String
    private lateinit var fcmToken: String

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = AccountSignupnickFragmentBinding.inflate(inflater, container, false)
        val view: View = binding.root

        firebaseAnalytics = FirebaseAnalytics.getInstance(requireContext())
        firebaseAuth = FirebaseAuth.getInstance()
        FirebaseMessaging.getInstance().token
            .addOnCompleteListener { task: Task<String?> ->
                if (!task.isSuccessful) {
                    Utils.log(task.exception.toString())
                    return@addOnCompleteListener
                }

                val token = task.result
                Utils.log(token.toString())
                fcmToken = token!!
            }
        accountService =
            RetrofitBuilder.create(requireContext(), RetrofitServices.AccountService::class.java)

        binding.apply {
            accountSignupnickNicknameEdittext.textChanged {
                binding.accountSignupnickNicknameLayout.error = null
            }
            accountSignupnickNicknameEdittext.setOnEditorActionListener { v, actionId, event ->
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    if (Utils.pwValidation(binding.accountSignupnickNicknameEdittext.text.toString()) == Utils.PWValidate.LENGTH) {
                        binding.accountSignupnickNicknameLayout.error =
                            "닉네임 길이를 확인 해 주세요. 6자 이상, 10자 이하로 입력해주세요."
                    } else if (Utils.pwValidation(binding.accountSignupnickNicknameEdittext.text.toString()) == Utils.PWValidate.INVALID) {
                        binding.accountSignupnickNicknameLayout.error =
                            "닉네임 형식을 확인 해 주세요. 숫자, 알파벳 대소문자, 한글만 사용가능합니다."
                    } else {
                        binding.accountSignupnickNicknameLayout.error = null
                        (requireActivity() as BaseActivity).hideKeyboard(binding.accountSignupnickNicknameLayout)
                    }
                    true
                } else
                    false
            }

            accountSignupnickOk.setOnClickListener { signUpWithEmailPW() }
        }

        idToken = arguments?.get("token") as String

        return view
    }


    private fun signUpWithEmailPW() {
        val nickname = binding.accountSignupnickNicknameEdittext.text.toString()

        if (Utils.nicknameValidate(nickname) != Utils.EmailValidate.VALID)
            binding.accountSignupnickNicknameLayout.requestFocus()
        else {
            (requireActivity() as BaseActivity).progressON(requireActivity())
            CoroutineScope(Dispatchers.Main).launch {
                runCatching {
                    accountService.createUser(idToken, fcmToken, firebaseAuth.uid!!, nickname)
                }.onSuccess {
                    if (it.code() == 403)
                        App.showToast("이미 존재하는 닉네임입니다.")
                    else {
                        App.showToast("회원 가입 되었습니다.")
                        val bundle = Bundle().apply {
                            putString(
                                FirebaseAnalytics.Param.ITEM_ID,
                                getString(R.string.analytics_id_signup)
                            )
                            putString(
                                FirebaseAnalytics.Param.ITEM_NAME,
                                getString(R.string.analytics_name_signup)
                            )
                            putString(
                                FirebaseAnalytics.Param.CONTENT_TYPE,
                                getString(R.string.analytics_type_text)
                            )
                        }
                        firebaseAnalytics.logEvent(FirebaseAnalytics.Event.SIGN_UP, bundle)

                        (requireActivity() as AccountActivity).startMain(it.body()!!)
                    }
                }.onFailure {
                    (requireActivity() as BaseActivity).progressOFF()
                    RetrofitBuilder.defaultErrorHandler(it)
                }
            }
        }
    }
}