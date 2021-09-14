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
import com.yhjoo.dochef.databinding.MainRecipesFragmentBinding
import com.yhjoo.dochef.db.DataGenerator
import com.yhjoo.dochef.model.MultiItemRecipe
import com.yhjoo.dochef.model.Recipe
import com.yhjoo.dochef.ui.activities.RecipeDetailActivity
import com.yhjoo.dochef.ui.adapter.RecipeMultiAdapter
import com.yhjoo.dochef.utilities.RetrofitBuilder
import com.yhjoo.dochef.utilities.RetrofitServices.RecipeService
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.*

class MainRecipesFragment : Fragment(), OnRefreshListener {
    /*
        TODO
        Recommend multi adapter 변경
    */

    companion object VALUES {
        object SORT {
            const val LATEST = 0
            const val POPULAR = 1
            const val RATING = 2
        }
    }

    private lateinit var binding: MainRecipesFragmentBinding
    private lateinit var recipeService: RecipeService
    private lateinit var recipeMultiAdapter: RecipeMultiAdapter
    private lateinit var recommendTags: Array<String>
    private var recipeListItems = ArrayList<MultiItemRecipe>()
    private var currentMode = SORT.LATEST

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = MainRecipesFragmentBinding.inflate(inflater, container, false)
        val view: View = binding.root

        recipeService = RetrofitBuilder.create(requireContext(), RecipeService::class.java)

        binding.apply {
            fRecipeSwipe.setOnRefreshListener(this@MainRecipesFragment)
            fRecipeSwipe.setColorSchemeColors(resources.getColor(R.color.colorPrimary, null))
            recipeMultiAdapter = RecipeMultiAdapter(recipeListItems).apply {
                setEmptyView(
                    R.layout.rv_loading,
                    fRecipeRecycler.parent as ViewGroup
                )
                showNew = true
                setOnItemClickListener { adapter: BaseQuickAdapter<*, *>, _: View?, position: Int ->
                    if (adapter.getItemViewType(position) == RecipeMultiAdapter.VIEWHOLDER_ITEM) {
                        val intent =
                            Intent(
                                this@MainRecipesFragment.context,
                                RecipeDetailActivity::class.java
                            )
                                .putExtra(
                                    "recipeID",
                                    (adapter.data[position] as MultiItemRecipe).content!!.recipeID
                                )
                        startActivity(intent)
                    }
                }
            }
            fRecipeRecycler.layoutManager = LinearLayoutManager(requireContext())
            fRecipeRecycler.adapter = recipeMultiAdapter

            recommendTags = resources.getStringArray(R.array.recommend_tags)
        }

        return view
    }

    override fun onRefresh() {
        Handler().postDelayed({ getRecipeList(currentMode) }, 1000)
    }

    override fun onResume() {
        super.onResume()
        if (App.isServerAlive) getRecipeList(currentMode) else {
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

    private fun getRecipeList(sort: Int) = CoroutineScope(Dispatchers.Main).launch {
        runCatching {
            var sortmode = ""
            when (sort) {
                SORT.LATEST -> sortmode = "latest"
                SORT.POPULAR -> sortmode = "popular"
                SORT.RATING -> sortmode = "rating"
            }

            val res1 = recipeService.getRecipes(sortmode)
            val arrayList = res1.body()!!
            recipeListItems.clear()
            for (i in arrayList.indices) {
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
            recipeMultiAdapter.setNewData(recipeListItems as List<MultiItemRecipe?>?)
            binding.fRecipeRecycler.layoutManager!!.scrollToPosition(0)
            binding.fRecipeSwipe.isRefreshing = false
        }
            .onSuccess { }
            .onFailure {
                RetrofitBuilder.defaultErrorHandler(it)
            }
    }

    fun changeSortMode(sort: Int) {
        if (currentMode != sort) {
            recipeMultiAdapter.setNewData(ArrayList())
            recipeMultiAdapter.setEmptyView(
                R.layout.rv_loading,
                binding.fRecipeRecycler.parent as ViewGroup
            )
            currentMode = sort
            getRecipeList(currentMode)
        }
    }
}