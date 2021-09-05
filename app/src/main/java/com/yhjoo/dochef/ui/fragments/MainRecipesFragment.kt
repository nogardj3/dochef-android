package com.yhjoo.dochef.ui.fragments

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.view.*
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout.OnRefreshListener
import com.chad.library.adapter.base.BaseQuickAdapter
import com.yhjoo.dochef.App
import com.yhjoo.dochef.R
import com.yhjoo.dochef.activities.BaseActivity
import com.yhjoo.dochef.activities.RecipeDetailActivity
import com.yhjoo.dochef.adapter.RecipeMultiAdapter
import com.yhjoo.dochef.databinding.FMainRecipesBinding
import com.yhjoo.dochef.utils.RxRetrofitServices.RecipeService
import com.yhjoo.dochef.model.*
import com.yhjoo.dochef.data.DataGenerator
import com.yhjoo.dochef.data.model.MultiItemRecipe
import com.yhjoo.dochef.data.model.Recipe
import com.yhjoo.dochef.utils.RxRetrofitBuilder
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import retrofit2.Response
import java.util.*

class MainRecipesFragment : Fragment(), OnRefreshListener {
    enum class SORT {
        LATEST, POPULAR, RATING
    }

    var binding: FMainRecipesBinding? = null
    var recipeService: RecipeService? = null
    var recipeMultiAdapter: RecipeMultiAdapter? = null
    var recipeListItems = ArrayList<MultiItemRecipe>()
    var recommend_tags: Array<String>
    var currentMode = SORT.LATEST

    /*
        TODO
        Recommend multi adapter 변경
    */
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FMainRecipesBinding.inflate(inflater, container, false)
        val view: View = binding!!.root
        recipeService = RxRetrofitBuilder.create(
            this.context,
            RecipeService::class.java
        )
        binding!!.fRecipeSwipe.setOnRefreshListener(this)
        binding!!.fRecipeSwipe.setColorSchemeColors(resources.getColor(R.color.colorPrimary, null))
        recipeMultiAdapter = RecipeMultiAdapter(recipeListItems)
        recipeMultiAdapter!!.setEmptyView(
            R.layout.rv_loading,
            binding!!.fRecipeRecycler.parent as ViewGroup
        )
        recipeMultiAdapter!!.setShowNew(true)
        recipeMultiAdapter!!.setOnItemClickListener { adapter: BaseQuickAdapter<*, *>, view1: View?, position: Int ->
            if (adapter.getItemViewType(position) == RecipeMultiAdapter.Companion.VIEWHOLDER_ITEM) {
                val intent =
                    Intent(this@MainRecipesFragment.context, RecipeDetailActivity::class.java)
                        .putExtra(
                            "recipeID",
                            (adapter.data[position] as MultiItemRecipe).content.recipeID
                        )
                startActivity(intent)
            }
        }
        binding!!.fRecipeRecycler.layoutManager = LinearLayoutManager(this.context)
        binding!!.fRecipeRecycler.adapter = recipeMultiAdapter
        recommend_tags = resources.getStringArray(R.array.recommend_tags)
        return view
    }

    override fun onRefresh() {
        Handler().postDelayed({ getRecipeList(currentMode) }, 1000)
    }

    override fun onResume() {
        super.onResume()
        if (App.isServerAlive()) getRecipeList(currentMode) else {
            val temp = DataGenerator.make<ArrayList<Recipe>>(
                resources,
                resources.getInteger(R.integer.DATE_TYPE_RECIPE)
            )
            for (i in temp.indices) {
                if (i != 0 && i % 4 == 0) {
                    if (i / 4 % 2 == 0) recipeListItems.add(MultiItemRecipe(RecipeMultiAdapter.VIEWHOLDER_AD))
                }
                recipeListItems.add(
                    MultiItemRecipe(
                        RecipeMultiAdapter.VIEWHOLDER_ITEM,
                        temp[i]
                    )
                )
            }
        }
    }

    fun getRecipeList(sort: SORT) {
        var sortmode = ""
        if (sort == SORT.LATEST) sortmode = "latest" else if (sort == SORT.POPULAR) sortmode =
            "popular" else if (sort == SORT.RATING) sortmode = "rating"
        (activity as BaseActivity?).getCompositeDisposable().add(
            recipeService!!.getRecipes(sortmode)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ response: Response<ArrayList<Recipe?>?>? ->
                    val arrayList = response!!.body()
                    val r = Random()
                    recipeListItems.clear()
                    for (i in arrayList!!.indices) {
                        if (i != 0 && i % 4 == 0) {
                            recipeListItems.add(MultiItemRecipe(RecipeMultiAdapter.VIEWHOLDER_AD))
                        }
                        recipeListItems.add(
                            MultiItemRecipe(
                                RecipeMultiAdapter.VIEWHOLDER_ITEM,
                                arrayList[i]
                            )
                        )
                    }
                    recipeMultiAdapter!!.setNewData(recipeListItems)
                    binding!!.fRecipeRecycler.layoutManager!!.scrollToPosition(0)
                    binding!!.fRecipeSwipe.isRefreshing = false
                }, RxRetrofitBuilder.defaultConsumer())
        )
    }

    fun changeSortMode(sort: SORT) {
        if (currentMode != sort) {
            recipeMultiAdapter!!.setNewData(ArrayList())
            recipeMultiAdapter!!.notifyDataSetChanged()
            recipeMultiAdapter!!.setEmptyView(
                R.layout.rv_loading,
                binding!!.fRecipeRecycler.parent as ViewGroup
            )
            currentMode = sort
            getRecipeList(currentMode)
        }
    }
}