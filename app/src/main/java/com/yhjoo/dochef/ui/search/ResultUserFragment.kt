package com.yhjoo.dochef.ui.search

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.activityViewModels
import com.yhjoo.dochef.Constants
import com.yhjoo.dochef.data.model.UserBrief
import com.yhjoo.dochef.databinding.SearchResultFragmentBinding
import com.yhjoo.dochef.ui.base.BaseFragment
import com.yhjoo.dochef.ui.home.HomeActivity
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ResultUserFragment : BaseFragment() {
    private lateinit var binding: SearchResultFragmentBinding
    private val searchViewModel: SearchViewModel by activityViewModels()

    private lateinit var userListAdapter: UserListAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = SearchResultFragmentBinding.inflate(layoutInflater)

        binding.apply {
            lifecycleOwner = viewLifecycleOwner

            userListAdapter = UserListAdapter(this@ResultUserFragment)
            resultRecycler.adapter = userListAdapter
        }

        searchViewModel.queriedUsers.observe(viewLifecycleOwner, {
            binding.resultinitGroup.isVisible = false
            binding.resultEmpty.isVisible = it.isEmpty()
            userListAdapter.submitList(it)
        })

        return binding.root
    }

    fun goHome(item: UserBrief) {
        startActivity(
            Intent(context, HomeActivity::class.java)
                .putExtra(Constants.INTENTNAME.USER_ID, item.userID)
        )
    }
}