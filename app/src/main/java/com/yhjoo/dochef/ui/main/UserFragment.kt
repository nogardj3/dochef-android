package com.yhjoo.dochef.ui.main

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.activityViewModels
import com.yhjoo.dochef.App
import com.yhjoo.dochef.Constants
import com.yhjoo.dochef.R
import com.yhjoo.dochef.data.repository.PostRepository
import com.yhjoo.dochef.data.repository.RecipeRepository
import com.yhjoo.dochef.data.repository.UserRepository
import com.yhjoo.dochef.databinding.MainUserFragmentBinding
import com.yhjoo.dochef.ui.base.BaseFragment
import com.yhjoo.dochef.ui.home.HomeActivity
import com.yhjoo.dochef.ui.recipe.RecipeMyListActivity
import com.yhjoo.dochef.ui.setting.SettingActivity
import com.yhjoo.dochef.utils.OtherUtil

class UserFragment : BaseFragment() {
    private lateinit var binding: MainUserFragmentBinding
    private val mainViewModel: MainViewModel by activityViewModels{
        MainViewModelFactory(
            UserRepository(requireContext().applicationContext),
            RecipeRepository(requireContext().applicationContext),
            PostRepository(requireContext().applicationContext)
        )
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding =
            DataBindingUtil.inflate(inflater, R.layout.main_user_fragment, container, false)

        binding.apply {
            lifecycleOwner = viewLifecycleOwner
            fragment = this@UserFragment
            viewModel = mainViewModel
        }

        return binding.root
    }

    fun goHome() {
        startActivity(Intent(requireContext(), HomeActivity::class.java))
    }

    fun goMyRecipe() {
        startActivity(
            Intent(requireContext(), RecipeMyListActivity::class.java)
                .putExtra(Constants.INTENTNAME.USER_ID, mainViewModel.activeUserId)
        )
    }

    fun goSetting() {
        startActivity(Intent(requireContext(), SettingActivity::class.java))
    }

    fun goReview() {
        try {
            startActivity(
                Intent(Intent.ACTION_VIEW)
                    .setData(
                        Uri.parse(
                            "https://play.google.com/store/apps/details?id=com.yhjoo.dochef"
                        )
                    )
                    .setPackage("com.android.vending")
            )
        } catch (e: Exception) {
            OtherUtil.log(e.toString())
            App.showToast("스토어 열기 실패")
        }
    }
}