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
import com.yhjoo.dochef.data.DataGenerator
import com.yhjoo.dochef.data.model.MultiItemRecipe
import com.yhjoo.dochef.data.model.Recipe
import com.yhjoo.dochef.databinding.FMainMyrecipeBinding
import com.yhjoo.dochef.utils.RxRetrofitServices.RecipeService
import com.yhjoo.dochef.model.*
import com.yhjoo.dochef.utils.*
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import retrofit2.Response
import java.util.*

class MainMyRecipeFragment : Fragment(), OnRefreshListener {
    var binding: FMainMyrecipeBinding? = null
    var recipeService: RecipeService? = null
    var recipeMultiAdapter: RecipeMultiAdapter? = null
    var recipeListItems = ArrayList<MultiItemRecipe>()
    var recommend_tags: Array<String>
    var userID: String? = null

    /*
        TODO
        Recommend multi adapter 변경
    */
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FMainMyrecipeBinding.inflate(inflater, container, false)
        val view: View = binding!!.root
        recipeService = RxRetrofitBuilder.create(
            this.context,
            RecipeService::class.java
        )
        binding!!.fMyrecipeSwipe.setOnRefreshListener(this)
        binding!!.fMyrecipeSwipe.setColorSchemeColors(
            resources.getColor(
                R.color.colorPrimary,
                null
            )
        )
        userID = Utils.getUserBrief(this.context).userID
        Utils.log(userID)
        recipeMultiAdapter = RecipeMultiAdapter(recipeListItems)
        recipeMultiAdapter!!.setUserid(userID)
        recipeMultiAdapter!!.setEmptyView(
            R.layout.rv_loading,
            binding!!.fMyrecipeRecycler.parent as ViewGroup
        )
        recipeMultiAdapter!!.setShowYours(true)
        recipeMultiAdapter!!.setOnItemClickListener { adapter: BaseQuickAdapter<*, *>, view1: View?, position: Int ->
            if (adapter.getItemViewType(position) == RecipeMultiAdapter.Companion.VIEWHOLDER_ITEM) {
                val intent =
                    Intent(this@MainMyRecipeFragment.context, RecipeDetailActivity::class.java)
                        .putExtra("recipeID", recipeListItems[position].content.recipeID)
                startActivity(intent)
            }
        }
        binding!!.fMyrecipeRecycler.layoutManager = LinearLayoutManager(this.context)
        binding!!.fMyrecipeRecycler.adapter = recipeMultiAdapter
        recommend_tags = resources.getStringArray(R.array.recommend_tags)
        return view
    }

    override fun onRefresh() {
        Handler().postDelayed({ recipelist }, 1000)
    }

    override fun onResume() {
        super.onResume()
        if (App.isServerAlive()) recipelist else {
            val temp = DataGenerator.make<ArrayList<Recipe>>(
                resources,
                resources.getInteger(R.integer.DATE_TYPE_RECIPE)
            )
            val r = Random()
            for (i in temp.indices) {
                if (i != 0 && i % 4 == 0) recipeListItems.add(MultiItemRecipe(RecipeMultiAdapter.VIEWHOLDER_AD))
                recipeListItems.add(
                    MultiItemRecipe(
                        RecipeMultiAdapter.VIEWHOLDER_ITEM,
                        temp[i]
                    )
                )
            }
            recipeMultiAdapter!!.setNewData(recipeListItems)
        }
    }

    val recipelist: Unit
        get() {
            (activity as BaseActivity?).getCompositeDisposable().add(
                recipeService!!.getRecipeByUserID(userID, "latest")
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe({ response: Response<ArrayList<Recipe?>?>? ->
                        val temp = response!!.body()
                        val r = Random()
                        recipeListItems.clear()
                        for (i in temp!!.indices) {
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
                        recipeMultiAdapter!!.setNewData(recipeListItems)
                        recipeMultiAdapter!!.setEmptyView(
                            R.layout.rv_empty_recipe,
                            binding!!.fMyrecipeRecycler.parent as ViewGroup
                        )
                        binding!!.fMyrecipeSwipe.isRefreshing = false
                    }, RxRetrofitBuilder.defaultConsumer())
            )
        }
}