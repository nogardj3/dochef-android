package com.yhjoo.dochef.ui.main

import android.content.Intent
import android.os.Bundle
import android.view.*
import androidx.core.view.isVisible
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.activityViewModels
import com.yhjoo.dochef.Constants
import com.yhjoo.dochef.R
import com.yhjoo.dochef.data.model.Recipe
import com.yhjoo.dochef.data.repository.PostRepository
import com.yhjoo.dochef.data.repository.RecipeRepository
import com.yhjoo.dochef.data.repository.UserRepository
import com.yhjoo.dochef.databinding.MainRecipesFragmentBinding
import com.yhjoo.dochef.ui.base.BaseFragment
import com.yhjoo.dochef.ui.recipe.RecipeDetailActivity
import java.util.*

class RecipesFragment : BaseFragment() {
    // TODO
    // swipe refresh

    private lateinit var binding: MainRecipesFragmentBinding
    private val mainViewModel: MainViewModel by activityViewModels {
        MainViewModelFactory(
            UserRepository(requireContext().applicationContext),
            RecipeRepository(requireContext().applicationContext),
            PostRepository(requireContext().applicationContext)
        )
    }

    private lateinit var recipesListAdapter: RecipesListAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding =
            DataBindingUtil.inflate(inflater, R.layout.main_recipes_fragment, container, false)

        binding.apply {
            lifecycleOwner = viewLifecycleOwner

            recipesSwipe.apply {
                setOnRefreshListener {
                    mainViewModel.refreshRecipesList()
                }
                setColorSchemeColors(
                    resources.getColor(
                        R.color.colorPrimary,
                        null
                    )
                )
            }

            recipesListAdapter = RecipesListAdapter(this@RecipesFragment)
            recipesRecycler.adapter = recipesListAdapter
        }

        mainViewModel.allRecipesList.observe(viewLifecycleOwner, {
            binding.recipesEmpty.isVisible = it.isEmpty()
            recipesListAdapter.submitList(it) {
                binding.recipesRecycler.scrollToPosition(0)
            }
            binding.recipesSwipe.isRefreshing = false
        })

        return binding.root
    }

    fun goRecipeDetail(item: Recipe) {
        startActivity(
            Intent(requireContext(), RecipeDetailActivity::class.java)
                .putExtra(Constants.INTENTNAME.RECIPE_ID, item.recipeID)
        )
    }
}