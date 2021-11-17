package com.yhjoo.dochef.ui.search

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.activityViewModels
import com.yhjoo.dochef.databinding.SearchResultFragmentBinding
import com.yhjoo.dochef.ui.base.BaseFragment
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ResultIngredientFragment : BaseFragment() {
    private lateinit var binding: SearchResultFragmentBinding
    private val searchViewModel: SearchViewModel by activityViewModels()

    private lateinit var recipeListAdapter: RecipeListAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = SearchResultFragmentBinding.inflate(layoutInflater)

        binding.apply {
            lifecycleOwner = viewLifecycleOwner

            recipeListAdapter =
                RecipeListAdapter(requireActivity() as SearchActivity, RecipeListAdapter.RESULTTYPE.INGREDIENT)

            resultRecycler.adapter = recipeListAdapter

            searchViewModel.queriedRecipeByIngredient.observe(viewLifecycleOwner, {
                resultinitGroup.isVisible = false
                resultEmpty.isVisible = it.isEmpty()
                recipeListAdapter.submitList(it)
            })
        }

        return binding.root
    }

}