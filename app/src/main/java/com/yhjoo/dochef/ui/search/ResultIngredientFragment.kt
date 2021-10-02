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
import com.yhjoo.dochef.data.model.Recipe
import com.yhjoo.dochef.data.repository.RecipeRepository
import com.yhjoo.dochef.data.repository.UserRepository
import com.yhjoo.dochef.databinding.SearchResultFragmentBinding
import com.yhjoo.dochef.ui.recipe.RecipeDetailActivity

class ResultIngredientFragment : Fragment() {
    private lateinit var binding: SearchResultFragmentBinding
    private val recipeViewModel: SearchViewModel by activityViewModels {
        SearchViewModelFactory(
            requireActivity().application,
            UserRepository(requireContext().applicationContext),
            RecipeRepository(requireContext().applicationContext)
        )
    }

    private lateinit var recipeAdapter: RecipeAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = SearchResultFragmentBinding.inflate(layoutInflater)

        binding.apply {
            lifecycleOwner = viewLifecycleOwner

            recipeAdapter =
                RecipeAdapter(RecipeAdapter.CONSTANTS.LAYOUT_TYPE.INGREDIENT) {
                    goDetail(it)
                }

            resultRecycler.apply {
                layoutManager = LinearLayoutManager(requireContext())
                adapter = recipeAdapter
            }

            recipeViewModel.queriedRecipeByIngredient.observe(viewLifecycleOwner, {
                resultinitGroup.isVisible = false
                resultEmpty.isVisible = it.isEmpty()
                recipeAdapter.submitList(it)
            })
        }

        return binding.root
    }

    private fun goDetail(item : Recipe){
        startActivity(
            Intent(context, RecipeDetailActivity::class.java)
                .putExtra("recipeID", item.recipeID))
    }
}