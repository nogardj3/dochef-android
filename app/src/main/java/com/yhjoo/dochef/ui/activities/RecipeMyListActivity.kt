package com.yhjoo.dochef.ui.activities

import android.content.Intent
import android.os.Bundle
import android.view.*
import androidx.recyclerview.widget.LinearLayoutManager
import com.afollestad.materialdialogs.MaterialDialog
import com.chad.library.adapter.base.BaseQuickAdapter
import com.yhjoo.dochef.App
import com.yhjoo.dochef.R
import com.yhjoo.dochef.databinding.ARecipelistBinding
import com.yhjoo.dochef.db.DataGenerator
import com.yhjoo.dochef.model.Recipe
import com.yhjoo.dochef.adapter.RecipeMyListAdapter
import com.yhjoo.dochef.utilities.*
import com.yhjoo.dochef.utilities.RetrofitServices.RecipeService
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.*

class RecipeMyListActivity : BaseActivity() {
    private val binding: ARecipelistBinding by lazy { ARecipelistBinding.inflate(layoutInflater) }

    private lateinit var recipeService: RecipeService
    private lateinit var recipeMyListAdapter: RecipeMyListAdapter
    private lateinit var addMenu: MenuItem
    private lateinit var recipeList: ArrayList<Recipe>
    private lateinit var userID: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        setSupportActionBar(binding.recipelistToolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        recipeService = RetrofitBuilder.create(this, RecipeService::class.java)
        userID = Utils.getUserBrief(this).userID

        binding.apply {
            recipeMyListAdapter = RecipeMyListAdapter(userID)
            recipeMyListAdapter.apply {
                setEmptyView(
                    R.layout.rv_loading,
                    recipelistRecycler.parent as ViewGroup
                )
                setOnItemClickListener { _: BaseQuickAdapter<*, *>?, _: View?, position: Int ->
                    val intent = Intent(this@RecipeMyListActivity, RecipeDetailActivity::class.java)
                        .putExtra("recipeID", recipeList[position].recipeID)
                    startActivity(intent)
                }
                setOnItemChildClickListener { adapter: BaseQuickAdapter<*, *>, view: View, position: Int ->
                    if (view.id == R.id.recipemylist_yours) {
                        MaterialDialog(this@RecipeMyListActivity).show {
                            message(text = "레시피를 삭제 하시겠습니까?")
                            positiveButton(text = "확인") {
                                cancelLikeRecipe(
                                    (adapter.data[position] as Recipe).recipeID
                                )
                            }
                            negativeButton(text = "취소")
                        }
                    }
                }
            }
            recipelistRecycler.apply {
                layoutManager = LinearLayoutManager(this@RecipeMyListActivity)
                adapter = recipeMyListAdapter
            }
        }
    }

    override fun onResume() {
        super.onResume()

        if (App.isServerAlive)
            loadData()
        else {
            recipeList = DataGenerator.make(
                resources, resources.getInteger(R.integer.DATE_TYPE_RECIPE)
            )
            recipeMyListAdapter.setNewData(recipeList)
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

    private fun loadData() = CoroutineScope(Dispatchers.Main).launch {
        runCatching {
            val res1 = recipeService.getRecipeByUserID(userID, "latest")
            recipeList = res1.body()!!
            recipeMyListAdapter.apply {
                setNewData(recipeList)
                setEmptyView(
                    R.layout.rv_empty_recipe,
                    binding.recipelistRecycler.parent as ViewGroup
                )
            }
        }
            .onSuccess { }
            .onFailure {
                RetrofitBuilder.defaultErrorHandler(it)
            }
    }

    private fun cancelLikeRecipe(recipeid: Int) = CoroutineScope(Dispatchers.Main).launch {
        runCatching {
            recipeService.setLikeRecipe(recipeid, userID, -1)
            loadData()
        }
            .onSuccess { }
            .onFailure {
                RetrofitBuilder.defaultErrorHandler(it)
            }
    }
}