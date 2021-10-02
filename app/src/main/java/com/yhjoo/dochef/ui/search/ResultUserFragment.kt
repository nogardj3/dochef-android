package com.yhjoo.dochef.ui.search

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.yhjoo.dochef.data.model.UserBrief
import com.yhjoo.dochef.data.repository.RecipeRepository
import com.yhjoo.dochef.data.repository.UserRepository
import com.yhjoo.dochef.databinding.SearchResultFragmentBinding
import com.yhjoo.dochef.ui.home.HomeActivity

class ResultUserFragment : Fragment() {
    private lateinit var binding: SearchResultFragmentBinding
    private val userViewModel: SearchViewModel by activityViewModels {
        SearchViewModelFactory(
            requireActivity().application,
            UserRepository(requireContext().applicationContext),
            RecipeRepository(requireContext().applicationContext)
        )
    }
    private lateinit var userListAdapter: UserListAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = SearchResultFragmentBinding.inflate(layoutInflater)

        binding.apply {
            lifecycleOwner = viewLifecycleOwner

            userListAdapter = UserListAdapter {
                goHome(it)
            }

            resultRecycler.apply {
                layoutManager = LinearLayoutManager(requireContext())
                adapter = userListAdapter
            }

            userViewModel.queriedUsers.observe(viewLifecycleOwner, {
                resultinitGroup.isVisible = false
                resultEmpty.isVisible = it.isEmpty()
                userListAdapter.submitList(it)
            })
        }

        return binding.root
    }

    private fun goHome(item: UserBrief) {
        startActivity(
            Intent(context, HomeActivity::class.java)
                .putExtra("userID", item.userID)
        )
    }
}