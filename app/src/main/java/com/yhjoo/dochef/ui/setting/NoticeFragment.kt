package com.yhjoo.dochef.ui.setting

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.yhjoo.dochef.R
import com.yhjoo.dochef.data.repository.BasicRepository
import com.yhjoo.dochef.databinding.SettingNoticeFragmentBinding
import com.yhjoo.dochef.ui.common.adapter.ExpandableListAdapter
import com.yhjoo.dochef.ui.common.viewmodel.BasicViewModel
import com.yhjoo.dochef.ui.common.viewmodel.BasicViewModelFactory

class NoticeFragment : Fragment() {
    private lateinit var binding: SettingNoticeFragmentBinding
    private lateinit var noticeViewModel: BasicViewModel
    private lateinit var expandableListAdapter: ExpandableListAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding =
            DataBindingUtil.inflate(inflater, R.layout.setting_notice_fragment, container, false)
        val view: View = binding.root

        val factory = BasicViewModelFactory(
            BasicRepository(requireContext().applicationContext)
        )

        noticeViewModel = factory.create(BasicViewModel::class.java).apply {
            allNotices.observe(viewLifecycleOwner, {
                expandableListAdapter.submitList(it) {
                    binding.noticeRecycler.scrollToPosition(0)
                }
            })
        }

        binding.apply {
            lifecycleOwner = viewLifecycleOwner

            expandableListAdapter = ExpandableListAdapter(
                true
            )

            noticeRecycler.apply {
                layoutManager = LinearLayoutManager(requireContext())
                adapter = expandableListAdapter
            }

            noticeViewModel.requestNotices()
        }

        return view
    }
}