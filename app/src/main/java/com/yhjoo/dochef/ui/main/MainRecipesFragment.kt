package com.yhjoo.dochef.ui.main

import android.content.Intent
import android.os.Bundle
import android.view.*
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout.OnRefreshListener
import com.yhjoo.dochef.R
import com.yhjoo.dochef.ui.common.adapter.RecipeListVerticalAdapter
import com.yhjoo.dochef.databinding.MainRecipesFragmentBinding
import com.yhjoo.dochef.data.repository.RecipeRepository
import com.yhjoo.dochef.ui.recipe.RecipeDetailActivity
import com.yhjoo.dochef.ui.common.viewmodel.RecipeListViewModel
import com.yhjoo.dochef.ui.common.viewmodel.RecipeListViewModelFactory
import java.util.*

class MainRecipesFragment : Fragment(), OnRefreshListener {
    /* TODO
    1. sort
    2. ad + item + recommend
     */

    private lateinit var binding: MainRecipesFragmentBinding
    private lateinit var recipeListViewModel: RecipeListViewModel
    private lateinit var recipeListVerticalAdapter: RecipeListVerticalAdapter

    private lateinit var recommendTags: Array<String>
    private var currentSort = RecipeListVerticalAdapter.Companion.SORT.LATEST

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding =
            DataBindingUtil.inflate(inflater, R.layout.main_recipes_fragment, container, false)
        val view: View = binding.root

        val factory = RecipeListViewModelFactory(
            RecipeRepository(
                requireContext().applicationContext
            )
        )

        recipeListViewModel = factory.create(RecipeListViewModel::class.java).apply {
            allRecipeList.observe(viewLifecycleOwner, {
                recipeListVerticalAdapter.submitList(it) {
                    binding.recipesRecycler.scrollToPosition(0)
                }
                binding.recipesSwipe.isRefreshing = false
            })
        }

        binding.apply {
            lifecycleOwner = viewLifecycleOwner

            recipesSwipe.apply {
                setOnRefreshListener(this@MainRecipesFragment)
                setColorSchemeColors(
                    resources.getColor(
                        R.color.colorPrimary,
                        null
                    )
                )
            }

            recipeListVerticalAdapter = RecipeListVerticalAdapter(
                RecipeListVerticalAdapter.MAIN_RECIPES,
                activeUserID = null,
                { item ->
                    val intent =
                        Intent(
                            this@MainRecipesFragment.requireContext(),
                            RecipeDetailActivity::class.java
                        )
                            .putExtra("recipeID", item.recipeID)
                    startActivity(intent)
                }
            )

            recipesRecycler.apply {
                layoutManager = LinearLayoutManager(requireContext())
                adapter = recipeListVerticalAdapter
            }

            recipeListViewModel.requestRecipeList(
                searchby = RecipeRepository.Companion.SEARCHBY.ALL,
                sort = currentSort,
                searchValue = null
            )

            recommendTags = resources.getStringArray(R.array.recommend_tags)
        }

        return view
    }

    override fun onRefresh() {
        binding.recipesSwipe.isRefreshing = true
        recipeListViewModel.requestRecipeList(
            searchby = RecipeRepository.Companion.SEARCHBY.ALL,
            sort = currentSort,
            searchValue = null
        )
    }

    fun changeSortMode(sort: String) {
        if (currentSort != sort) {
            currentSort = sort
            onRefresh()
        }
    }
}