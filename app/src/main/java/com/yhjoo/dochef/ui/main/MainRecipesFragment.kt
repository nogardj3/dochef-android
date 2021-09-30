package com.yhjoo.dochef.ui.main

import android.content.Intent
import android.os.Bundle
import android.view.*
import androidx.core.view.isVisible
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout.OnRefreshListener
import com.yhjoo.dochef.R
import com.yhjoo.dochef.data.repository.PostRepository
import com.yhjoo.dochef.data.repository.RecipeRepository
import com.yhjoo.dochef.data.repository.UserRepository
import com.yhjoo.dochef.databinding.MainRecipesFragmentBinding
import com.yhjoo.dochef.ui.common.adapter.RecipeListVerticalAdapter
import com.yhjoo.dochef.ui.recipe.RecipeDetailActivity
import java.util.*

class MainRecipesFragment : Fragment(), OnRefreshListener {
    /* TODO
    1. ad + item + recommend
     */

    private lateinit var binding: MainRecipesFragmentBinding
    private val mainViewModel: MainViewModel by activityViewModels {
        MainViewModelFactory(
            UserRepository(requireContext().applicationContext),
            RecipeRepository(requireContext().applicationContext),
            PostRepository(requireContext().applicationContext)
        )
    }
    private lateinit var recipeListVerticalAdapter: RecipeListVerticalAdapter

    private lateinit var recommendTags: Array<String>

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding =
            DataBindingUtil.inflate(inflater, R.layout.main_recipes_fragment, container, false)
        val view: View = binding.root

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
                RecipeListVerticalAdapter.CONSTANTS.LAYOUT_TYPE.MAIN_RECIPES,
                activeUserID = mainViewModel.userId.value,
                itemClickListener = { item ->
                    Intent(
                        this@MainRecipesFragment.requireContext(),
                        RecipeDetailActivity::class.java
                    )
                        .putExtra("recipeID", item.recipeID).apply {
                            startActivity(this)
                        }
                },
                null
            )

            recipesRecycler.apply {
                layoutManager = LinearLayoutManager(requireContext())
                adapter = recipeListVerticalAdapter
            }

            mainViewModel.allRecipesList.observe(viewLifecycleOwner, {
                recipesEmpty.isVisible = it.isEmpty()
                recipeListVerticalAdapter.submitList(it) {
                    binding.recipesRecycler.scrollToPosition(0)
                }
                binding.recipesSwipe.isRefreshing = false
            })

            mainViewModel.refreshRecipesList()

            recommendTags = resources.getStringArray(R.array.recommend_tags)
        }

        return view
    }

    override fun onRefresh() {
        binding.recipesSwipe.isRefreshing = true
        mainViewModel.refreshRecipesList()
    }
}