package com.yhjoo.dochef.ui.fragments

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import com.yhjoo.dochef.App
import com.yhjoo.dochef.R
import com.yhjoo.dochef.data.DataGenerator
import com.yhjoo.dochef.data.model.UserDetail
import com.yhjoo.dochef.databinding.FMainUserBinding
import com.yhjoo.dochef.ui.activities.BaseActivity
import com.yhjoo.dochef.ui.activities.HomeActivity
import com.yhjoo.dochef.ui.activities.RecipeMyListActivity
import com.yhjoo.dochef.ui.activities.SettingActivity
import com.yhjoo.dochef.utils.*
import com.yhjoo.dochef.utils.RxRetrofitServices.UserService
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers

class MainUserFragment : Fragment() {
    private lateinit var binding: FMainUserBinding
    private lateinit var userService: UserService
    private lateinit var userDetailInfo: UserDetail
    private lateinit var userID: String

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FMainUserBinding.inflate(inflater, container, false)
        val view: View = binding.root

        userService = RxRetrofitBuilder.create(requireContext(), UserService::class.java)

        userID = Utils.getUserBrief(requireContext()).userID

        binding.apply {
            fmainUserHome.setOnClickListener { goHome() }
            fmainUserRecipe.setOnClickListener { goMyRecipe() }
            fmainUserSetting.setOnClickListener { goSetting() }
            fmainUserReview.setOnClickListener { goReview() }
        }
        return view
    }

    override fun onResume() {
        super.onResume()
        if (App.isServerAlive)
            settingUserDetail()
        else {
            userDetailInfo =
                DataGenerator.make(resources, resources.getInteger(R.integer.DATA_TYPE_USER_DETAIL))
            ImageLoadUtil.loadUserImage(
                requireContext(),
                userDetailInfo.userImg,
                binding.fmainUserImg
            )
            binding.fmainUserNickname.text = userDetailInfo.nickname
        }
    }

    private fun goHome() {
        startActivity(Intent(context, HomeActivity::class.java))
    }

    private fun goMyRecipe() {
        val intent = Intent(context, RecipeMyListActivity::class.java)
            .putExtra("userID", userDetailInfo.userID)
        startActivity(intent)
    }

    private fun goSetting() {
        startActivity(Intent(context, SettingActivity::class.java))
    }

    private fun goReview() {
        val intent = Intent(Intent.ACTION_VIEW)
        intent.setData(
            Uri.parse(
                "https://play.google.com/store/apps/details?id=com.yhjoo.dochef"
            )
        )
            .setPackage("com.android.vending")
        try {
            startActivity(intent)
        } catch (e: Exception) {
            Utils.log(e.toString())
            App.showToast("스토어 열기 실패")
        }
    }

    private fun settingUserDetail() {
        (activity as BaseActivity).compositeDisposable.add(
            userService.getUserDetail(userID)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    userDetailInfo = it.body()!!
                    ImageLoadUtil.loadUserImage(
                        this@MainUserFragment.requireContext(),
                        userDetailInfo.userImg,
                        binding!!.fmainUserImg
                    )
                    binding!!.fmainUserNickname.text = userDetailInfo.nickname
                }, RxRetrofitBuilder.defaultConsumer())
        )
    }
}