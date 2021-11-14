package com.yhjoo.dochef.ui.search

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.activityViewModels
import com.yhjoo.dochef.Constants
import com.yhjoo.dochef.data.model.Recipe
import com.yhjoo.dochef.databinding.SearchResultFragmentBinding
import com.yhjoo.dochef.ui.base.BaseFragment
import com.yhjoo.dochef.ui.recipe.RecipeDetailActivity
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ResultTagFragment : BaseFragment() {
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

            recipeListAdapter = RecipeListAdapter(requireActivity() as SearchActivity, RecipeListAdapter.RESULTTYPE.TAG)

            resultRecycler.adapter = recipeListAdapter
        }

        searchViewModel.queriedRecipeByTag.observe(viewLifecycleOwner, {
            binding.resultinitGroup.isVisible = false
            binding.resultEmpty.isVisible = it.isEmpty()
            recipeListAdapter.submitList(it)
        })

        return binding.root
    }

    fun goDetail(item: Recipe) {
        startActivity(
            Intent(context, RecipeDetailActivity::class.java)
                .putExtra(Constants.INTENTNAME.RECIPE_ID, item.recipeID)
        )
    }
}