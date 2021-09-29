package com.yhjoo.dochef.ui.recipe

import android.content.Intent
import android.os.Bundle
import android.view.*
import androidx.activity.viewModels
import androidx.core.view.isVisible
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.GridLayoutManager
import com.yhjoo.dochef.R
import com.yhjoo.dochef.RECIPE
import com.yhjoo.dochef.data.repository.RecipeRepository
import com.yhjoo.dochef.databinding.RecipethemeActivityBinding
import com.yhjoo.dochef.ui.base.BaseActivity
import com.yhjoo.dochef.ui.common.adapter.RecipeListVerticalAdapter
import com.yhjoo.dochef.ui.common.adapter.RecipeListVerticalAdapter.Companion.LayoutType.THEME
import com.yhjoo.dochef.ui.common.viewmodel.RecipeListViewModel
import com.yhjoo.dochef.ui.common.viewmodel.RecipeListViewModelFactory
import java.util.*

class RecipeThemeActivity : BaseActivity() {
    /* TODO
    1. query strategy by intent
    2. ad + item
     */

    private val binding: RecipethemeActivityBinding by lazy {
        DataBindingUtil.setContentView(this, R.layout.recipetheme_activity)
    }
    private val recipeListViewModel: RecipeListViewModel by viewModels {
        RecipeListViewModelFactory(
            RecipeRepository(applicationContext)
        )
    }
    private lateinit var recipeListVerticalAdapter: RecipeListVerticalAdapter

    private lateinit var tagName: String
    private var currentMode: Int? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        setSupportActionBar(binding.recipethemeToolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        if (intent.getStringExtra("tag") == null)
            currentMode = RECIPE.THEME.POPULAR
        else {
            currentMode = RECIPE.THEME.TAG
            tagName = intent.getStringExtra("tag")!!
        }

        binding.apply {
            lifecycleOwner = this@RecipeThemeActivity

            recipeListVerticalAdapter = RecipeListVerticalAdapter(
                THEME,
                activeUserID = null,
                itemClickListener = { item ->
                    Intent(this@RecipeThemeActivity, RecipeDetailActivity::class.java)
                        .putExtra("recipeID", item.recipeID).apply {
                            startActivity(this)
                        }
                },
                null
            )

            recipethemeRecycler.apply {
                layoutManager = GridLayoutManager(this@RecipeThemeActivity, 2)
                adapter = recipeListVerticalAdapter
            }

            recipeListViewModel.allRecipeList.observe(this@RecipeThemeActivity, {
                recipethemeEmpty.isVisible = it.isEmpty()
                recipeListVerticalAdapter.submitList(it) {
                    binding.recipethemeRecycler.scrollToPosition(0)
                }
            })

            if (currentMode == RECIPE.THEME.POPULAR) {
                recipeListViewModel.requestRecipeList(
                    searchby = RECIPE.SEARCHBY.ALL,
                    sort = RECIPE.SORT.POPULAR,
                    searchValue = null
                )
            } else {
                recipeListViewModel.requestRecipeList(
                    searchby = RECIPE.SEARCHBY.TAG,
                    sort = RECIPE.SORT.POPULAR,
                    searchValue = tagName
                )
            }
        }
    }
}