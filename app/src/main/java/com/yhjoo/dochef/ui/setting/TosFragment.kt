package com.yhjoo.dochef.ui.setting

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.activityViewModels
import com.yhjoo.dochef.R
import com.yhjoo.dochef.data.repository.BasicRepository
import com.yhjoo.dochef.databinding.SettingTosFragmentBinding
import com.yhjoo.dochef.ui.base.BaseFragment

class TosFragment : BaseFragment() {
    private lateinit var binding: SettingTosFragmentBinding
    private val settingViewModel: SettingViewModel by activityViewModels {
        SettingViewModelFactory(
            BasicRepository(requireContext().applicationContext)
        )
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(inflater, R.layout.setting_tos_fragment, container, false)

        binding.apply {
            lifecycleOwner = viewLifecycleOwner
            viewModel = settingViewModel
        }

        return binding.root
    }
}