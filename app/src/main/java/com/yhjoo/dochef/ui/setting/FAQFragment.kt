package com.yhjoo.dochef.ui.setting

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.yhjoo.dochef.R
import com.yhjoo.dochef.data.repository.BasicRepository
import com.yhjoo.dochef.databinding.SettingFaqFragmentBinding

class FAQFragment : Fragment() {
    private lateinit var binding: SettingFaqFragmentBinding
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
            DataBindingUtil.inflate(inflater, R.layout.setting_faq_fragment, container, false)

        binding.apply {
            lifecycleOwner = viewLifecycleOwner

            expandableListAdapter = ExpandableListAdapter(
                false
            )

            faqRecycler.apply {
                layoutManager = LinearLayoutManager(requireContext())
                adapter = expandableListAdapter
            }

            settingViewModel.allFAQs.observe(viewLifecycleOwner, {
                faqEmpty.isVisible = it.isEmpty()
                expandableListAdapter.submitList(it) {
                    binding.faqRecycler.scrollToPosition(0)
                }
            })

            settingViewModel.requestFAQs()
        }

        return binding.root
    }
}