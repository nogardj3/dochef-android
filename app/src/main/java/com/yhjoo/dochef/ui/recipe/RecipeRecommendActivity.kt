package com.yhjoo.dochef.ui.recipe

import android.content.Intent
import android.os.Bundle
import android.view.*
import androidx.activity.viewModels
import androidx.core.view.isVisible
import androidx.databinding.DataBindingUtil
import com.yhjoo.dochef.Constants
import com.yhjoo.dochef.R
import com.yhjoo.dochef.data.model.Recipe
import com.yhjoo.dochef.data.repository.RecipeRepository
import com.yhjoo.dochef.databinding.ReciperecommendActivityBinding
import com.yhjoo.dochef.ui.base.BaseActivity
import java.util.*

class RecipeRecommendActivity : BaseActivity() {
    private val binding: ReciperecommendActivityBinding by lazy {
        DataBindingUtil.setContentView(this, R.layout.reciperecommend_activity)
    }
    private val recipeRecommendViewModel: RecipeRecommendViewModel by viewModels {
        RecipeRecommendViewModelFactory(
            RecipeRepository(applicationContext),
            intent
        )
    }
    private lateinit var recipeRecommendAdapter: RecipeRecommendAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        setSupportActionBar(binding.reciperecommendToolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        binding.apply {
            lifecycleOwner = this@RecipeRecommendActivity

            recipeRecommendAdapter = RecipeRecommendAdapter(this@RecipeRecommendActivity)
            reciperecommendRecycler.adapter = recipeRecommendAdapter
        }

        recipeRecommendViewModel.allRecipeList.observe(this@RecipeRecommendActivity, {
            binding.reciperecommendEmpty.isVisible = it.isEmpty()
            recipeRecommendAdapter.submitList(it) {
                binding.reciperecommendRecycler.scrollToPosition(0)
            }
        })
    }

    fun goDetail(item: Recipe) {
        startActivity(
            Intent(this@RecipeRecommendActivity, RecipeDetailActivity::class.java)
                .putExtra(Constants.INTENTNAME.RECIPE_ID, item.recipeID)
        )
    }
}