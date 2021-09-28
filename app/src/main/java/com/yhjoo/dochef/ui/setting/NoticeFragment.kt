package com.yhjoo.dochef.ui.setting

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.yhjoo.dochef.R
import com.yhjoo.dochef.data.repository.BasicRepository
import com.yhjoo.dochef.databinding.SettingNoticeFragmentBinding

class NoticeFragment : Fragment() {
    private lateinit var binding: SettingNoticeFragmentBinding
    private val settingViewModel: SettingViewModel by activityViewModels {
        SettingViewModelFactory(
            BasicRepository(requireContext().applicationContext)
        )
    }
    private lateinit var expandableListAdapter: ExpandableListAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding =
            DataBindingUtil.inflate(inflater, R.layout.setting_notice_fragment, container, false)
        val view: View = binding.root

        binding.apply {
            lifecycleOwner = viewLifecycleOwner

            expandableListAdapter = ExpandableListAdapter(true)

            noticeRecycler.apply {
                layoutManager = LinearLayoutManager(requireContext())
                adapter = expandableListAdapter
            }

            settingViewModel.allNotices.observe(viewLifecycleOwner, {
                expandableListAdapter.submitList(it) {
                    binding.noticeRecycler.scrollToPosition(0)
                }
            })

            settingViewModel.requestNotices()
        }

        return view
    }
}