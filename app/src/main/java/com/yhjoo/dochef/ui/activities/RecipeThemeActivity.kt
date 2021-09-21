package com.yhjoo.dochef.ui.activities

import android.content.Intent
import android.os.Bundle
import android.view.*
import androidx.recyclerview.widget.GridLayoutManager
import com.chad.library.adapter.base.BaseQuickAdapter
import com.google.android.gms.ads.MobileAds
import com.yhjoo.dochef.App
import com.yhjoo.dochef.R
import com.yhjoo.dochef.databinding.ARecipethemeBinding
import com.yhjoo.dochef.db.DataGenerator
import com.yhjoo.dochef.model.MultiItemTheme
import com.yhjoo.dochef.model.Recipe
import com.yhjoo.dochef.adapter.RecipeMultiThemeAdapter
import com.yhjoo.dochef.utilities.RetrofitBuilder
import com.yhjoo.dochef.utilities.RetrofitServices.RecipeService
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.*

class RecipeThemeActivity : BaseActivity() {
    /*
        TODO
        Recommend multi adapter 변경
    */

    object VIEWHOLDER {
        const val AD = 1
        const val ITEM = 2
    }

    object MODE {
        const val POPULAR = 0
        const val TAG = 1
    }

    private val binding: ARecipethemeBinding by lazy { ARecipethemeBinding.inflate(layoutInflater) }
    private lateinit var recipeService: RecipeService
    private lateinit var recipeMultiThemeAdapter: RecipeMultiThemeAdapter
    private lateinit var recipeListItems: ArrayList<MultiItemTheme>

    private lateinit var tagName: String
    private var currentMode: Int? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        setSupportActionBar(binding.recipethemeToolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        MobileAds.initialize(this)
        recipeService = RetrofitBuilder.create(this, RecipeService::class.java)
        if (intent.getStringExtra("tag") == null) currentMode = MODE.POPULAR else {
            currentMode = MODE.TAG
            tagName = intent.getStringExtra("tag")!!
        }

        binding.apply {
            recipeMultiThemeAdapter = RecipeMultiThemeAdapter(recipeListItems).apply {
                setSpanSizeLookup { _: GridLayoutManager?, position: Int -> recipeListItems[position].spanSize }
                onItemClickListener =
                    BaseQuickAdapter.OnItemClickListener { adapter: BaseQuickAdapter<*, *>, _: View?, position: Int ->
                        if (adapter.getItemViewType(position) == VIEWHOLDER.ITEM) {
                            val intent =
                                Intent(this@RecipeThemeActivity, RecipeDetailActivity::class.java)
                                    .putExtra(
                                        "recipeID",
                                        recipeListItems[position].content!!.recipeID
                                    )
                            startActivity(intent)
                        }
                    }
            }
            recipethemeRecycler.apply{
                layoutManager = GridLayoutManager(this@RecipeThemeActivity, 2)
                adapter = recipeMultiThemeAdapter
            }
        }
    }

    override fun onResume() {
        super.onResume()
        if (App.isServerAlive) {
            loadData()
        } else {
            val arrayList = DataGenerator.make<ArrayList<Recipe>>(
                resources, resources.getInteger(R.integer.DATE_TYPE_RECIPE)
            )
            for (i in arrayList.indices) {
                if (i != 0 && i % 4 == 0)
                    recipeListItems.add(MultiItemTheme(VIEWHOLDER.AD, 2))
                recipeListItems.add(MultiItemTheme(VIEWHOLDER.ITEM, 1, arrayList[i]))
            }
            recipeMultiThemeAdapter.setNewData(recipeListItems as List<MultiItemTheme?>?)
        }
    }

    private fun loadData() = CoroutineScope(Dispatchers.Main).launch {
        runCatching {
            val res1 = if (currentMode == MODE.POPULAR)
                recipeService.getRecipes("popular")
            else
                recipeService.getRecipeByTag(tagName, "popular")

            val arrayList = res1.body()!!
            for (i in arrayList.indices) {
                if (i != 0 && i % 4 == 0)
                    recipeListItems.add(MultiItemTheme(VIEWHOLDER.AD, 2))
                recipeListItems.add(MultiItemTheme(VIEWHOLDER.ITEM, 1, arrayList[i]))
            }
            recipeMultiThemeAdapter.setNewData(recipeListItems as List<MultiItemTheme?>?)
        }
            .onSuccess { }
            .onFailure {
                RetrofitBuilder.defaultErrorHandler(it)
            }
    }
}