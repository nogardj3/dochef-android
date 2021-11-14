package com.yhjoo.dochef.ui.setting

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.activityViewModels
import com.yhjoo.dochef.R
import com.yhjoo.dochef.databinding.SettingNoticeFragmentBinding
import com.yhjoo.dochef.ui.base.BaseFragment
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class NoticeFragment : BaseFragment() {
    private lateinit var binding: SettingNoticeFragmentBinding
    private val settingViewModel: SettingViewModel by activityViewModels()

    private lateinit var expandableListAdapter: ExpandableListAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding =
            DataBindingUtil.inflate(inflater, R.layout.setting_notice_fragment, container, false)

        binding.apply {
            lifecycleOwner = viewLifecycleOwner

            expandableListAdapter = ExpandableListAdapter(true)
            noticeRecycler.adapter = expandableListAdapter
        }

        settingViewModel.allNotices.observe(viewLifecycleOwner, {
            binding.noticeEmpty.isVisible = it.isEmpty()
            expandableListAdapter.submitList(it) {
                binding.noticeRecycler.scrollToPosition(0)
            }
        })

        return binding.root
    }
}