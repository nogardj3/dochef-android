package com.yhjoo.dochef.ui.setting

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import com.yhjoo.dochef.R
import com.yhjoo.dochef.data.repository.BasicRepository
import com.yhjoo.dochef.databinding.SettingTosFragmentBinding
import com.yhjoo.dochef.ui.common.viewmodel.BasicViewModel
import com.yhjoo.dochef.ui.common.viewmodel.BasicViewModelFactory

class TosFragment : Fragment() {
    private lateinit var binding: SettingTosFragmentBinding
    private lateinit var tosViewModel: BasicViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(inflater, R.layout.setting_tos_fragment, container, false)
        val view: View = binding.root

        val factory = BasicViewModelFactory(
            BasicRepository(requireContext().applicationContext)
        )

        tosViewModel = factory.create(BasicViewModel::class.java).apply {
            tosText.observe(viewLifecycleOwner, {
                binding.tosText.text = it
            })
        }
        binding.apply {
            lifecycleOwner = viewLifecycleOwner

            tosViewModel.requestTosText()
        }

        return view
    }
}