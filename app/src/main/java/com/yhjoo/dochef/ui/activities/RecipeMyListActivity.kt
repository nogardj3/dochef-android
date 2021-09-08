package com.yhjoo.dochef.ui.activities

import android.content.Intent
import android.os.Bundle
import android.view.*
import androidx.recyclerview.widget.LinearLayoutManager
import com.afollestad.materialdialogs.MaterialDialog
import com.chad.library.adapter.base.BaseQuickAdapter
import com.yhjoo.dochef.App
import com.yhjoo.dochef.R
import com.yhjoo.dochef.activities.RecipeMakeActivity
import com.yhjoo.dochef.ui.adapter.RecipeMyListAdapter
import com.yhjoo.dochef.data.DataGenerator
import com.yhjoo.dochef.data.model.Recipe
import com.yhjoo.dochef.databinding.ARecipelistBinding
import com.yhjoo.dochef.utils.*
import com.yhjoo.dochef.utils.RetrofitServices.RecipeService
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
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
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)

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

            recipelistRecycler.layoutManager = LinearLayoutManager(this@RecipeMyListActivity)
            recipelistRecycler.adapter = recipeMyListAdapter
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
        if (item.itemId == R.id.menu_recipe_add) startActivity(
            Intent(
                this,
                RecipeMakeActivity::class.java
            )
        )
        return super.onOptionsItemSelected(item)
    }

    private fun loadData() {
        compositeDisposable.add(
            recipeService.getRecipeByUserID(userID, "latest")
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    recipeList = it.body()!!
                    recipeMyListAdapter.setNewData(recipeList)
                    recipeMyListAdapter.setEmptyView(
                        R.layout.rv_empty_recipe,
                        binding.recipelistRecycler.parent as ViewGroup
                    )
                }, RetrofitBuilder.defaultConsumer())
        )
    }

    private fun cancelLikeRecipe(recipeid: Int) {
        compositeDisposable.add(
            recipeService.setLikeRecipe(recipeid, userID, -1)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                    { loadData() },
                    RetrofitBuilder.defaultConsumer()
                )
        )
    }
}