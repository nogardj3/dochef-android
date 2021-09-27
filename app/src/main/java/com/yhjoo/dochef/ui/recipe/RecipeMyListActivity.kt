package com.yhjoo.dochef.ui.recipe

import android.content.Intent
import android.os.Bundle
import android.view.*
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import com.yhjoo.dochef.R
import com.yhjoo.dochef.ui.common.adapter.RecipeListVerticalAdapter
import com.yhjoo.dochef.databinding.RecipemylistActivityBinding
import com.yhjoo.dochef.data.network.RetrofitBuilder
import com.yhjoo.dochef.data.repository.RecipeRepository
import com.yhjoo.dochef.ui.base.BaseActivity
import com.yhjoo.dochef.utils.*
import com.yhjoo.dochef.ui.common.viewmodel.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.*

class RecipeMyListActivity : BaseActivity() {
    /* TODO
    1. dislike and refresh recipe
    2. ad + item + recommend
     */

    val binding: RecipemylistActivityBinding by lazy {
        DataBindingUtil.setContentView(this, R.layout.recipemylist_activity)
    }
    private lateinit var recipeListViewModel: RecipeListViewModel
    private lateinit var recipeListVerticalAdapter: RecipeListVerticalAdapter

    private lateinit var addMenu: MenuItem
    private lateinit var userID: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        setSupportActionBar(binding.recipelistToolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        userID = DatastoreUtil.getUserBrief(this).userID

        val factory = RecipeListViewModelFactory(
            RecipeRepository(
                applicationContext
            )
        )

        recipeListViewModel = factory.create(RecipeListViewModel::class.java).apply {
            allRecipeList.observe(this@RecipeMyListActivity, {
                recipeListVerticalAdapter.submitList(it) {
                    binding.recipelistRecycler.scrollToPosition(0)
                }
            })
        }

        binding.apply {
            lifecycleOwner = this@RecipeMyListActivity

            recipeListVerticalAdapter = RecipeListVerticalAdapter(
                RecipeListVerticalAdapter.MAIN_MYRECIPE,
                activeUserID = userID,
                { item ->
                    val intent = Intent(this@RecipeMyListActivity, RecipeDetailActivity::class.java)
                        .putExtra("recipeID", item.recipeID)
                    startActivity(intent)
                }
            )

            recipelistRecycler.apply {
                layoutManager = LinearLayoutManager(this@RecipeMyListActivity)
                adapter = recipeListVerticalAdapter
            }

            recipeListViewModel.requestRecipeList(
                searchby = RecipeRepository.Companion.SEARCHBY.USERID,
                sort = RecipeListVerticalAdapter.Companion.SORT.LATEST,
                searchValue = userID
            )

//            if (view.id == R.id.recipemylist_yours) {
//                MaterialDialog(this@RecipeMyListActivity).show {
//                    message(text = "레시피를 삭제 하시겠습니까?")
//                    positiveButton(text = "확인") {
//                        cancelLikeRecipe(
//                            (adapter.data[position] as Recipe).recipeID
//                        )
//                    }
//                    negativeButton(text = "취소")
//                }
//            }
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

    private fun dislikeRecipe(recipeid: Int) = CoroutineScope(Dispatchers.Main).launch {
        runCatching {
            recipeListViewModel.disLikeRecipe(recipeid, userID)
            recipeListViewModel.requestRecipeList(
                searchby = RecipeRepository.Companion.SEARCHBY.USERID,
                sort = RecipeListVerticalAdapter.Companion.SORT.LATEST,
                searchValue = userID
            )
        }
            .onSuccess { }
            .onFailure {
                RetrofitBuilder.defaultErrorHandler(it)
            }
    }
}