package com.yhjoo.dochef.activities

import android.content.Intent
import android.os.Bundle
import android.view.*
import androidx.recyclerview.widget.GridLayoutManager
import com.chad.library.adapter.base.BaseQuickAdapter
import com.google.android.gms.ads.MobileAds
import com.yhjoo.dochef.App
import com.yhjoo.dochef.R
import com.yhjoo.dochef.adapter.RecipeMultiThemeAdapter
import com.yhjoo.dochef.databinding.ARecipethemeBinding
import com.yhjoo.dochef.utils.RxRetrofitServices.RecipeService
import com.yhjoo.dochef.model.*
import com.yhjoo.dochef.data.DataGenerator
import com.yhjoo.dochef.data.model.MultiItemTheme
import com.yhjoo.dochef.data.model.Recipe
import com.yhjoo.dochef.ui.activities.BaseActivity
import com.yhjoo.dochef.utils.RxRetrofitBuilder
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.Single
import retrofit2.Response
import java.util.*

class RecipeThemeActivity : BaseActivity() {
    val VIEWHOLDER_AD = 1
    val VIEWHOLDER_ITEM = 2

    object MODE {
        const val POPULAR = 0
        const val TAG = 1
    }

    var binding: ARecipethemeBinding? = null
    var recipeService: RecipeService? = null
    var recipeMultiThemeAdapter: RecipeMultiThemeAdapter? = null
    var recipeListItems = ArrayList<MultiItemTheme>()
    var tagName: String? = null
    var currentMode: Int? = null

    /*
        TODO
        Recommend multi adapter 변경
    */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ARecipethemeBinding.inflate(layoutInflater)
        setContentView(binding!!.root)
        setSupportActionBar(binding!!.recipethemeToolbar)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        MobileAds.initialize(this)
        if (intent.getStringExtra("tag") == null) currentMode = MODE.POPULAR else {
            currentMode = MODE.TAG
            tagName = intent.getStringExtra("tag")
        }
        recipeService = RxRetrofitBuilder.create(this, RecipeService::class.java)
        recipeMultiThemeAdapter = RecipeMultiThemeAdapter(recipeListItems)
        recipeMultiThemeAdapter!!.setSpanSizeLookup { gridLayoutManager: GridLayoutManager?, position: Int -> recipeListItems[position].spanSize }
        recipeMultiThemeAdapter!!.onItemClickListener =
            BaseQuickAdapter.OnItemClickListener { adapter: BaseQuickAdapter<*, *>, view: View?, position: Int ->
                if (adapter.getItemViewType(position) == VIEWHOLDER_ITEM) {
                    val intent = Intent(this@RecipeThemeActivity, RecipeDetailActivity::class.java)
                        .putExtra("recipeID", recipeListItems[position].content.recipeID)
                    startActivity(intent)
                }
            }
        binding!!.recipethemeRecycler.layoutManager = GridLayoutManager(this, 2)
        binding!!.recipethemeRecycler.adapter = recipeMultiThemeAdapter
    }

    override fun onResume() {
        super.onResume()
        if (App.appInstance.isServerAlive) {
            loadData()
        } else {
            val arrayList = DataGenerator.make<ArrayList<Recipe>>(
                resources, resources.getInteger(R.integer.DATE_TYPE_RECIPE)
            )
            for (i in arrayList.indices) {
                if (i != 0 && i % 4 == 0) recipeListItems.add(MultiItemTheme(VIEWHOLDER_AD, 2))
                recipeListItems.add(MultiItemTheme(VIEWHOLDER_ITEM, 1, arrayList[i]))
            }
            recipeMultiThemeAdapter!!.setNewData(recipeListItems)
        }
    }

    fun loadData() {
        val recipeSingle: Single<Response<ArrayList<Recipe?>?>?>
        recipeSingle = if (currentMode == MODE.POPULAR) recipeService!!.getRecipes("popular")
            .observeOn(AndroidSchedulers.mainThread()) else recipeService!!.getRecipeByTag(
            tagName,
            "popular"
        )
            .observeOn(AndroidSchedulers.mainThread())
        compositeDisposable!!.add(
            recipeSingle
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ response: Response<ArrayList<Recipe?>?>? ->
                    val arrayList = response!!.body()
                    for (i in arrayList!!.indices) {
                        if (i != 0 && i % 4 == 0) recipeListItems.add(
                            MultiItemTheme(
                                VIEWHOLDER_AD,
                                2
                            )
                        )
                        recipeListItems.add(MultiItemTheme(VIEWHOLDER_ITEM, 1, arrayList[i]))
                    }
                    recipeMultiThemeAdapter!!.setNewData(recipeListItems)
                }, RxRetrofitBuilder.defaultConsumer())
        )
    }
}