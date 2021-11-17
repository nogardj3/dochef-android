package com.yhjoo.dochef.ui.setting

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.activityViewModels
import com.yhjoo.dochef.R
import com.yhjoo.dochef.databinding.SettingFaqFragmentBinding
import com.yhjoo.dochef.ui.base.BaseFragment
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class FAQFragment : BaseFragment() {
    private lateinit var binding: SettingFaqFragmentBinding
    private val settingViewModel: SettingViewModel by activityViewModels()

    private lateinit var expandableListAdapter: ExpandableListAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding =
            DataBindingUtil.inflate(inflater, R.layout.setting_faq_fragment, container, false)

        binding.apply {
            lifecycleOwner = viewLifecycleOwner

            expandableListAdapter = ExpandableListAdapter(false)
            faqRecycler.adapter = expandableListAdapter
        }

        settingViewModel.allFAQs.observe(viewLifecycleOwner, {
            binding.faqEmpty.isVisible = it.isEmpty()
            expandableListAdapter.submitList(it) {
                binding.faqRecycler.scrollToPosition(0)
            }
        })

        return binding.root
    }
}