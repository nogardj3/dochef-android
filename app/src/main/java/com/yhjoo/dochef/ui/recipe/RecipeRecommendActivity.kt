package com.yhjoo.dochef.ui.recipe

import android.content.Intent
import android.os.Bundle
import android.view.*
import androidx.activity.viewModels
import androidx.core.view.isVisible
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.GridLayoutManager
import com.yhjoo.dochef.R
import com.yhjoo.dochef.data.model.Recipe
import com.yhjoo.dochef.data.repository.RecipeRepository
import com.yhjoo.dochef.databinding.ReciperecommendActivityBinding
import com.yhjoo.dochef.ui.base.BaseActivity
import com.yhjoo.dochef.ui.common.adapter.RecipeListVerticalAdapter
import com.yhjoo.dochef.ui.common.adapter.RecipeListVerticalAdapter.Companion.LayoutType.RECOMMEND
import java.util.*

class RecipeRecommendActivity : BaseActivity() {
    private val binding: ReciperecommendActivityBinding by lazy {
        DataBindingUtil.setContentView(this, R.layout.reciperecommend_activity)
    }
    private val recipeRecommendViewModel: RecipeRecommendViewModel by viewModels {
        RecipeRecommendViewModelFactory(
            application,
            intent,
            RecipeRepository(applicationContext)
        )
    }
    private lateinit var recipeListVerticalAdapter: RecipeListVerticalAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        setSupportActionBar(binding.reciperecommendToolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        binding.apply {
            lifecycleOwner = this@RecipeRecommendActivity

            recipeListVerticalAdapter = RecipeListVerticalAdapter(
                RECOMMEND,
                null,
                { goDetail(it) },
                null
            )

            reciperecommendRecycler.apply {
                layoutManager = GridLayoutManager(this@RecipeRecommendActivity, 2)
                adapter = recipeListVerticalAdapter
            }

            recipeRecommendViewModel.allRecipeList.observe(this@RecipeRecommendActivity, {
                reciperecommendEmpty.isVisible = it.isEmpty()
                recipeListVerticalAdapter.submitList(it) {
                    binding.reciperecommendRecycler.scrollToPosition(0)
                }
            })
        }
    }

    private fun goDetail(item: Recipe) {
        startActivity(
            Intent(this@RecipeRecommendActivity, RecipeDetailActivity::class.java)
                .putExtra("recipeID", item.recipeID)
        )
    }
}