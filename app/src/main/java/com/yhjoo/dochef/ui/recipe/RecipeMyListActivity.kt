package com.yhjoo.dochef.ui.recipe

import android.content.Intent
import android.os.Bundle
import android.view.*
import androidx.activity.viewModels
import androidx.core.view.isVisible
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import com.afollestad.materialdialogs.MaterialDialog
import com.yhjoo.dochef.Constants
import com.yhjoo.dochef.R
import com.yhjoo.dochef.data.repository.RecipeRepository
import com.yhjoo.dochef.databinding.RecipemylistActivityBinding
import com.yhjoo.dochef.ui.base.BaseActivity
import com.yhjoo.dochef.ui.common.adapter.RecipeListVerticalAdapter
import com.yhjoo.dochef.ui.common.adapter.RecipeListVerticalAdapter.CONSTANTS.LayoutType.MYLIST
import com.yhjoo.dochef.ui.common.viewmodel.*
import com.yhjoo.dochef.utils.*
import java.util.*

class RecipeMyListActivity : BaseActivity() {
    /* TODO
    1. ad + item + recommend
     */

    private val binding: RecipemylistActivityBinding by lazy {
        DataBindingUtil.setContentView(this, R.layout.recipemylist_activity)
    }
    private val recipeListViewModel: RecipeListViewModel by viewModels {
        RecipeListViewModelFactory(
            application,
            RecipeRepository(applicationContext)
        )
    }
    private lateinit var recipeListVerticalAdapter: RecipeListVerticalAdapter

    private lateinit var addMenu: MenuItem
    private lateinit var userID: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        setSupportActionBar(binding.recipelistToolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        userID = DatastoreUtil.getUserBrief(this).userID

        binding.apply {
            lifecycleOwner = this@RecipeMyListActivity

            recipeListVerticalAdapter = RecipeListVerticalAdapter(
                MYLIST,
                activeUserID = userID,
                { item ->
                    Intent(this@RecipeMyListActivity, RecipeDetailActivity::class.java)
                        .putExtra("recipeID", item.recipeID).apply {
                            startActivity(this)
                        }
                },
                childClickListener = { item ->
                    MaterialDialog(this@RecipeMyListActivity).show {
                        message(text = "즐겨찾기를 해제 하시겠습니까?")
                        positiveButton(text = "확인") {
                            recipeListViewModel.disLikeRecipe(item.recipeID, userID)

                            recipeListViewModel.requestRecipeList(
                                searchby = Constants.RECIPE.SEARCHBY.USERID,
                                sort = Constants.RECIPE.SORT.LATEST,
                                searchValue = userID
                            )
                        }
                        negativeButton(text = "취소")
                    }
                }
            )

            recipelistRecycler.apply {
                layoutManager = LinearLayoutManager(this@RecipeMyListActivity)
                adapter = recipeListVerticalAdapter
            }

            recipeListViewModel.allRecipeList.observe(this@RecipeMyListActivity, {
                recipeListVerticalAdapter.submitList(it) {
                    recipemylistEmpty.isVisible = it.isEmpty()
                    binding.recipelistRecycler.scrollToPosition(0)
                }
            })

            recipeListViewModel.listChanged.observe(this@RecipeMyListActivity, {
                if (it) {
                    recipeListViewModel.requestRecipeList(
                        searchby = Constants.RECIPE.SEARCHBY.USERID,
                        sort = Constants.RECIPE.SORT.LATEST,
                        searchValue = userID
                    )
                }
            })

            recipeListViewModel.requestRecipeList(
                searchby = Constants.RECIPE.SEARCHBY.USERID,
                sort = Constants.RECIPE.SORT.LATEST,
                searchValue = userID
            )
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_recipe_add, menu)
        addMenu = menu.findItem(R.id.menu_recipe_add)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return if (item.itemId == R.id.menu_recipe_add) {
            startActivity(Intent(this, RecipeMakeActivity::class.java))
            true
        } else
            super.onOptionsItemSelected(item)
    }
}