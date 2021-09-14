package com.yhjoo.dochef.ui.fragments

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatTextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.edit
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.chad.library.adapter.base.entity.MultiItemEntity
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.tasks.Task
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.messaging.FirebaseMessaging
import com.google.gson.Gson
import com.yhjoo.dochef.App
import com.yhjoo.dochef.R
import com.yhjoo.dochef.data.DataGenerator
import com.yhjoo.dochef.data.model.*
import com.yhjoo.dochef.databinding.*
import com.yhjoo.dochef.ui.activities.AccountActivity
import com.yhjoo.dochef.ui.activities.BaseActivity
import com.yhjoo.dochef.ui.activities.MainActivity
import com.yhjoo.dochef.ui.adapter.FAQListAdapter
import com.yhjoo.dochef.utils.ImageLoadUtil
import com.yhjoo.dochef.utils.RetrofitBuilder
import com.yhjoo.dochef.utils.RetrofitServices
import com.yhjoo.dochef.utils.Utils
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.ArrayList

class AccountSignUpNickFragment : Fragment() {
    private lateinit var binding: FAccountSignupnickBinding
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
        binding = FAccountSignupnickBinding.inflate(inflater, container, false)
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
            accountSignupnickOk.setOnClickListener { signUpWithEmailPW() }
        }

        idToken = arguments?.get("token") as String

        return view
    }


    private fun signUpWithEmailPW() {
        val nickname = binding.accountSignupnickNickname.text.toString()

        when {
            Utils.nicknameValidate(nickname) == Utils.NicknameValidate.NODATA ->
                App.showToast("닉네임을 입력 해 주세요.")
            Utils.nicknameValidate(nickname) == Utils.NicknameValidate.LENGTH ->
                App.showToast("닉네임의 길이를 확인 해 주세요. 6자 이상, 10자 이하로 입력해주세요")
            Utils.nicknameValidate(nickname) == Utils.NicknameValidate.INVALID ->
                App.showToast("사용할 수 없는 닉네임입니다. 숫자, 알파벳 대소문자, 한글만 사용가능합니다.")
            else -> {
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
}