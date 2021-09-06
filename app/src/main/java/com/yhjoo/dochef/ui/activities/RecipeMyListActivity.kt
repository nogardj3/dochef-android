package com.yhjoo.dochef.activities

import android.content.Intent
import android.os.Bundle
import android.view.*
import androidx.recyclerview.widget.LinearLayoutManager
import com.afollestad.materialdialogs.MaterialDialog
import com.chad.library.adapter.base.BaseQuickAdapter
import com.google.gson.JsonObject
import com.yhjoo.dochef.App
import com.yhjoo.dochef.R
import com.yhjoo.dochef.adapter.RecipeMyListAdapter
import com.yhjoo.dochef.data.DataGenerator
import com.yhjoo.dochef.data.model.Recipe
import com.yhjoo.dochef.databinding.ARecipelistBinding
import com.yhjoo.dochef.utils.RxRetrofitServices.RecipeService
import com.yhjoo.dochef.model.*
import com.yhjoo.dochef.ui.activities.BaseActivity
import com.yhjoo.dochef.utils.*
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import retrofit2.Response
import java.util.*

class RecipeMyListActivity : BaseActivity() {
    var binding: ARecipelistBinding? = null
    var recipeService: RecipeService? = null
    var recipeMyListAdapter: RecipeMyListAdapter? = null
    var addMenu: MenuItem? = null
    var recipeList = ArrayList<Recipe?>()
    var userID: String? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ARecipelistBinding.inflate(layoutInflater)
        setContentView(binding!!.root)
        setSupportActionBar(binding!!.recipelistToolbar)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        recipeService = RxRetrofitBuilder.create(this, RecipeService::class.java)
        userID = Utils.getUserBrief(this).userID
        recipeMyListAdapter = RecipeMyListAdapter(userID)
        recipeMyListAdapter!!.setEmptyView(
            R.layout.rv_loading,
            binding!!.recipelistRecycler.parent as ViewGroup
        )
        recipeMyListAdapter!!.setOnItemClickListener { adapter: BaseQuickAdapter<*, *>?, view: View?, position: Int ->
            val intent = Intent(this@RecipeMyListActivity, RecipeDetailActivity::class.java)
                .putExtra("recipeID", recipeList[position].getRecipeID())
            startActivity(intent)
        }
        recipeMyListAdapter!!.setOnItemChildClickListener { adapter: BaseQuickAdapter<*, *>, view: View, position: Int ->
            if (view.id == R.id.recipemylist_yours) {

                MaterialDialog(this).show{
                    message(text="레시피를 삭제 하시겠습니까?")
                    positiveButton(text="확인"){
                        cancelLikeRecipe(
                            (adapter.data[position] as Recipe).recipeID
                        )
                    }
                    negativeButton(text="취소")
                }
            }
        }
        binding!!.recipelistRecycler.layoutManager = LinearLayoutManager(this)
        binding!!.recipelistRecycler.adapter = recipeMyListAdapter
    }

    override fun onResume() {
        super.onResume()
        if (App.isServerAlive()) loadData() else {
            recipeList = DataGenerator.make(
                resources, resources.getInteger(R.integer.DATE_TYPE_RECIPE)
            )
            recipeMyListAdapter!!.setNewData(recipeList)
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

    fun loadData() {
        compositeDisposable!!.add(
            recipeService!!.getRecipeByUserID(userID, "latest")
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ response: Response<ArrayList<Recipe?>>? ->
                    recipeList = response!!.body()!!
                    recipeMyListAdapter!!.setNewData(recipeList)
                    recipeMyListAdapter!!.setEmptyView(
                        R.layout.rv_empty_recipe,
                        binding!!.recipelistRecycler.parent as ViewGroup
                    )
                }, RxRetrofitBuilder.defaultConsumer())
        )
    }

    fun cancelLikeRecipe(recipeid: Int) {
        compositeDisposable!!.add(
            recipeService!!.setLikeRecipe(recipeid, userID, -1)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                    { response: Response<JsonObject?>? -> loadData() },
                    RxRetrofitBuilder.defaultConsumer()
                )
        )
    }
}