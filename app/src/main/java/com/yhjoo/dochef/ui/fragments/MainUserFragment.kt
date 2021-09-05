package com.yhjoo.dochef.ui.fragments

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import com.yhjoo.dochef.App
import com.yhjoo.dochef.App.Companion.appInstance
import com.yhjoo.dochef.R
import com.yhjoo.dochef.activities.BaseActivity
import com.yhjoo.dochef.activities.HomeActivity
import com.yhjoo.dochef.activities.RecipeMyListActivity
import com.yhjoo.dochef.activities.SettingActivity
import com.yhjoo.dochef.data.DataGenerator
import com.yhjoo.dochef.databinding.FMainUserBinding
import com.yhjoo.dochef.utils.RxRetrofitServices.UserService
import com.yhjoo.dochef.data.model.UserDetail
import com.yhjoo.dochef.utils.*
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import retrofit2.Response

class MainUserFragment : Fragment() {
    var binding: FMainUserBinding? = null
    var userService: UserService? = null
    var userDetailInfo: UserDetail? = null
    var userID: String? = null
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FMainUserBinding.inflate(inflater, container, false)
        val view: View = binding!!.root
        userService = RxRetrofitBuilder.create(context, UserService::class.java)
        userID = Utils.getUserBrief(context).userID
        binding!!.fmainUserHome.setOnClickListener { view: View? -> goHome(view) }
        binding!!.fmainUserRecipe.setOnClickListener { view: View? -> goMyRecipe(view) }
        binding!!.fmainUserSetting.setOnClickListener { view: View? -> goSetting(view) }
        binding!!.fmainUserReview.setOnClickListener { view: View? -> goReview(view) }
        return view
    }

    override fun onResume() {
        super.onResume()
        if (App.isServerAlive()) userDetail else {
            userDetailInfo =
                DataGenerator.make(resources, resources.getInteger(R.integer.DATA_TYPE_USER_DETAIL))
            ImageLoadUtil.loadUserImage(
                context,
                userDetailInfo.getUserImg(),
                binding!!.fmainUserImg
            )
            binding!!.fmainUserNickname.text = userDetailInfo.getNickname()
        }
    }

    fun goHome(view: View?) {
        startActivity(Intent(context, HomeActivity::class.java))
    }

    fun goMyRecipe(view: View?) {
        val intent = Intent(context, RecipeMyListActivity::class.java)
            .putExtra("userID", userDetailInfo.getUserID())
        startActivity(intent)
    }

    fun goSetting(view: View?) {
        startActivity(Intent(context, SettingActivity::class.java))
    }

    fun goReview(view: View?) {
        val intent = Intent(Intent.ACTION_VIEW)
        intent.setData(
            Uri.parse(
                "https://play.google.com/store/apps/details?id=quvesoft.sprout"
            )
        )
            .setPackage("com.android.vending")
        try {
            startActivity(intent)
        } catch (e: Exception) {
            Utils.log(e.toString())
            appInstance!!.showToast("스토어 열기 실패")
        }
    }

    val userDetail: Unit
        get() {
            (activity as BaseActivity?).getCompositeDisposable().add(
                userService!!.getUserDetail(userID)
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe({ response: Response<UserDetail?>? ->
                        userDetailInfo = response!!.body()
                        ImageLoadUtil.loadUserImage(
                            this@MainUserFragment.context,
                            userDetailInfo.getUserImg(),
                            binding!!.fmainUserImg
                        )
                        binding!!.fmainUserNickname.text = userDetailInfo.getNickname()
                    }, RxRetrofitBuilder.defaultConsumer())
            )
        }
}