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
import com.yhjoo.dochef.data.model.Recipe
import com.yhjoo.dochef.data.repository.PostRepository
import com.yhjoo.dochef.data.repository.RecipeRepository
import com.yhjoo.dochef.data.repository.UserRepository
import com.yhjoo.dochef.databinding.MainRecipesFragmentBinding
import com.yhjoo.dochef.ui.common.adapter.RecipeListVerticalAdapter
import com.yhjoo.dochef.ui.recipe.RecipeDetailActivity
import java.util.*

class RecipesFragment : Fragment(), OnRefreshListener {
    private lateinit var binding: MainRecipesFragmentBinding
    private val mainViewModel: MainViewModel by activityViewModels (){
        MainViewModelFactory(
            requireActivity().application,
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

        binding.apply {
            lifecycleOwner = viewLifecycleOwner

            recipesSwipe.apply {
                setOnRefreshListener(this@RecipesFragment)
                setColorSchemeColors(
                    resources.getColor(
                        R.color.colorPrimary,
                        null
                    )
                )
            }

            recipeListVerticalAdapter = RecipeListVerticalAdapter(
                RecipeListVerticalAdapter.Companion.LayoutType.MAIN_RECIPES,
                mainViewModel.userId,
                { goRecipeDetail(it) },
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

            recommendTags = resources.getStringArray(R.array.recommend_tags)
        }

        return binding.root
    }

    override fun onRefresh() {
        binding.recipesSwipe.isRefreshing = true
        mainViewModel.refreshRecipesList()
    }

    private fun goRecipeDetail(item: Recipe) {
        startActivity(
            Intent(requireContext(), RecipeDetailActivity::class.java)
                .putExtra("recipeID", item.recipeID)
        )
    }
}