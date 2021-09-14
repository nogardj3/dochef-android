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
import com.yhjoo.dochef.databinding.MainMyrecipeFragmentBinding
import com.yhjoo.dochef.db.DataGenerator
import com.yhjoo.dochef.model.MultiItemRecipe
import com.yhjoo.dochef.model.Recipe
import com.yhjoo.dochef.ui.activities.RecipeDetailActivity
import com.yhjoo.dochef.ui.adapter.RecipeMultiAdapter
import com.yhjoo.dochef.utilities.*
import com.yhjoo.dochef.utilities.RetrofitServices.RecipeService
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.*

class MainMyRecipeFragment : Fragment(), OnRefreshListener {
    /*
        TODO
        Recommend multi adapter 변경
    */

    private lateinit var binding: MainMyrecipeFragmentBinding
    private lateinit var recipeService: RecipeService
    private lateinit var recipeMultiAdapter: RecipeMultiAdapter
    private lateinit var recommendTags: Array<String>
    private var recipeListItems = ArrayList<MultiItemRecipe>()
    private var userID: String? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = MainMyrecipeFragmentBinding.inflate(inflater, container, false)
        val view: View = binding.root

        recipeService = RetrofitBuilder.create(requireContext(), RecipeService::class.java)
        userID = Utils.getUserBrief(requireContext()).userID

        binding.apply {
            fMyrecipeSwipe.setOnRefreshListener(this@MainMyRecipeFragment)
            fMyrecipeSwipe.setColorSchemeColors(
                resources.getColor(
                    R.color.colorPrimary,
                    null
                )
            )

            recipeMultiAdapter = RecipeMultiAdapter(recipeListItems).apply {
                userid = userID
                setEmptyView(R.layout.rv_loading, fMyrecipeRecycler.parent as ViewGroup)
                showYours = true
                setOnItemClickListener { adapter: BaseQuickAdapter<*, *>, _: View?, position: Int ->
                    if (adapter.getItemViewType(position) == RecipeMultiAdapter.VIEWHOLDER_ITEM) {
                        val intent =
                            Intent(
                                this@MainMyRecipeFragment.context,
                                RecipeDetailActivity::class.java
                            )
                                .putExtra("recipeID", recipeListItems[position].content!!.recipeID)
                        startActivity(intent)
                    }
                }
            }
            fMyrecipeRecycler.layoutManager = LinearLayoutManager(requireContext())
            fMyrecipeRecycler.adapter = recipeMultiAdapter

            recommendTags = resources.getStringArray(R.array.recommend_tags)
        }

        return view
    }

    override fun onRefresh() {
        Handler().postDelayed({ settingList() }, 1000)
    }

    override fun onResume() {
        super.onResume()
        if (App.isServerAlive)
            settingList()
        else {
            val temp = DataGenerator.make<ArrayList<Recipe>>(
                resources,
                resources.getInteger(R.integer.DATE_TYPE_RECIPE)
            )

            for (i in temp.indices) {
                if (i != 0 && i % 4 == 0)
                    recipeListItems.add(MultiItemRecipe(RecipeMultiAdapter.VIEWHOLDER_AD))
                recipeListItems.add(
                    MultiItemRecipe(
                        RecipeMultiAdapter.VIEWHOLDER_ITEM,
                        temp[i]
                    )
                )
            }
            recipeMultiAdapter.setNewData(recipeListItems as List<MultiItemRecipe?>?)
        }
    }

    private fun settingList() = CoroutineScope(Dispatchers.Main).launch {
        runCatching {
            val res1 = recipeService.getRecipeByUserID(userID!!, "latest")

            val temp = res1.body()!!
            recipeListItems.clear()
            for (i in temp.indices) {
                if (i != 0 && i % 4 == 0) recipeListItems.add(
                    MultiItemRecipe(
                        RecipeMultiAdapter.VIEWHOLDER_AD
                    )
                )
                recipeListItems.add(
                    MultiItemRecipe(
                        RecipeMultiAdapter.VIEWHOLDER_ITEM,
                        temp[i]
                    )
                )
            }
            recipeMultiAdapter.setNewData(recipeListItems as List<MultiItemRecipe?>?)
            recipeMultiAdapter.setEmptyView(
                R.layout.rv_empty_recipe,
                binding.fMyrecipeRecycler.parent as ViewGroup
            )
            binding.fMyrecipeSwipe.isRefreshing = false
        }
            .onSuccess { }
            .onFailure {
                RetrofitBuilder.defaultErrorHandler(it)
            }
    }
}