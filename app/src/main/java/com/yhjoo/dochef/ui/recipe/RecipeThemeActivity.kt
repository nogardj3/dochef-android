package com.yhjoo.dochef.ui.recipe

import android.content.Intent
import android.os.Bundle
import android.view.*
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.GridLayoutManager
import com.yhjoo.dochef.R
import com.yhjoo.dochef.ui.common.adapter.RecipeVerticalListAdapter
import com.yhjoo.dochef.databinding.RecipethemeActivityBinding
import com.yhjoo.dochef.data.repository.RecipeRepository
import com.yhjoo.dochef.ui.base.BaseActivity
import com.yhjoo.dochef.ui.common.viewmodel.RecipeListViewModel
import com.yhjoo.dochef.ui.common.viewmodel.RecipeListViewModelFactory
import java.util.*

class RecipeThemeActivity : BaseActivity() {
    /* TODO
    1. query strategy by intent
    2. ad + item
     */

    object QUERY {
        const val POPULAR = 0
        const val TAG = 1
    }

    val binding: RecipethemeActivityBinding by lazy {
        DataBindingUtil.setContentView(this, R.layout.recipetheme_activity)
    }
    private lateinit var recipeListViewModel: RecipeListViewModel
    private lateinit var recipeListAdapter: RecipeVerticalListAdapter

    private lateinit var tagName: String
    private var currentMode: Int? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        setSupportActionBar(binding.recipethemeToolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        if (intent.getStringExtra("tag") == null)
            currentMode = QUERY.POPULAR
        else {
            currentMode = QUERY.TAG
            tagName = intent.getStringExtra("tag")!!
        }

        val factory = RecipeListViewModelFactory(
            RecipeRepository(
                applicationContext
            )
        )

        recipeListViewModel = factory.create(RecipeListViewModel::class.java).apply {
            allRecipeList.observe(this@RecipeThemeActivity, {
                recipeListAdapter.submitList(it) {
                    binding.recipethemeRecycler.scrollToPosition(0)
                }
            })
        }

        binding.apply {
            lifecycleOwner = this@RecipeThemeActivity

            recipeListAdapter = RecipeVerticalListAdapter(
                RecipeVerticalListAdapter.THEME,
                activeUserID = null,
                { item ->
                    val intent =
                        Intent(this@RecipeThemeActivity, RecipeDetailActivity::class.java)
                            .putExtra(
                                "recipeID",
                                item.recipeID
                            )
                    startActivity(intent)
                }
            )

            recipethemeRecycler.apply {
                layoutManager = GridLayoutManager(this@RecipeThemeActivity, 2)
                adapter = recipeListAdapter
            }


            if (currentMode == QUERY.POPULAR) {
                recipeListViewModel.requestRecipeList(
                    searchby = RecipeRepository.Companion.SEARCHBY.ALL,
                    sort = "popular",
                    searchValue = null
                )
            } else {
                recipeListViewModel.requestRecipeList(
                    searchby = RecipeRepository.Companion.SEARCHBY.TAG,
                    sort = "popular",
                    searchValue = tagName
                )
            }
        }
    }
}