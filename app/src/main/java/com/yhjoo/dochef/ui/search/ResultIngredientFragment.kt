package com.yhjoo.dochef.ui.search

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.chad.library.adapter.base.BaseQuickAdapter
import com.yhjoo.dochef.R
import com.yhjoo.dochef.data.model.Recipe
import com.yhjoo.dochef.data.model.UserBrief
import com.yhjoo.dochef.data.network.RetrofitBuilder
import com.yhjoo.dochef.data.network.RetrofitServices.RecipeService
import com.yhjoo.dochef.data.network.RetrofitServices.UserService
import com.yhjoo.dochef.data.repository.RecipeRepository
import com.yhjoo.dochef.data.repository.UserRepository
import com.yhjoo.dochef.databinding.SearchResultFragmentBinding
import com.yhjoo.dochef.ui.home.HomeActivity
import com.yhjoo.dochef.ui.recipe.RecipeDetailActivity
import com.yhjoo.dochef.utils.OtherUtil
import java.util.*

class ResultIngredientFragment : Fragment() {
    private lateinit var binding: SearchResultFragmentBinding
    private val recipeViewModel: SearchViewModel by activityViewModels(){
        SearchViewModelFactory(
            UserRepository(requireContext().applicationContext),
            RecipeRepository(requireContext().applicationContext)
        )
    }

    private lateinit var resultRecipeAdapter: ResultRecipeAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = SearchResultFragmentBinding.inflate(layoutInflater)
        val view: View = binding.root


        binding.apply {
            resultRecipeAdapter = ResultRecipeAdapter(
                ResultRecipeAdapter.INGREDIENT,
                { item ->
                    val intent = Intent(context, RecipeDetailActivity::class.java)
                        .putExtra("recipeID", item.recipeID)
                    startActivity(intent)
                }
            )

            resultRecycler.apply {
                layoutManager = LinearLayoutManager(requireContext())
                adapter = resultRecipeAdapter
            }

            recipeViewModel.keyword.observe(viewLifecycleOwner, {
                recipeViewModel.requestRecipeByIngredients(it!!)
            })

            recipeViewModel.queriedRecipeByIngredient.observe(viewLifecycleOwner, {
                resultinitGroup.isVisible = false
                resultRecycler.isVisible = it.isNotEmpty()
                resultEmpty.isVisible = it.isEmpty()

                resultRecipeAdapter.submitList(it)
            })
        }

        return view
    }
}