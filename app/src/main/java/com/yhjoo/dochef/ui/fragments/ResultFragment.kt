package com.yhjoo.dochef.ui.fragments

import android.content.Intent
import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.chad.library.adapter.base.BaseQuickAdapter
import com.yhjoo.dochef.App
import com.yhjoo.dochef.R
import com.yhjoo.dochef.ui.activities.BaseActivity
import com.yhjoo.dochef.ui.activities.HomeActivity
import com.yhjoo.dochef.activities.RecipeDetailActivity
import com.yhjoo.dochef.activities.SearchActivity
import com.yhjoo.dochef.adapter.SearchListAdapter
import com.yhjoo.dochef.data.DataGenerator
import com.yhjoo.dochef.data.model.Recipe
import com.yhjoo.dochef.data.model.SearchResult
import com.yhjoo.dochef.data.model.UserBrief
import com.yhjoo.dochef.databinding.FResultBinding
import com.yhjoo.dochef.utils.RxRetrofitServices.RecipeService
import com.yhjoo.dochef.utils.RxRetrofitServices.UserService
import com.yhjoo.dochef.model.*
import com.yhjoo.dochef.utils.*
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.Single
import retrofit2.Response
import java.util.*

class ResultFragment : Fragment() {
    private val VIEWHOLDER_AD = 0
    private val VIEWHOLDER_ITEM_USER = 1
    private val VIEWHOLDER_ITEM_RECIPE_NAME = 2
    private val VIEWHOLDER_ITEM_INGREDIENT = 3
    private val VIEWHOLDER_ITEM_TAG = 4
    var binding: FResultBinding? = null
    var userService: UserService? = null
    var recipeService: RecipeService? = null
    var searchListAdapter: SearchListAdapter? = null
    var keyword: String? = null
    var type = 0

    /*
        TODO
    */
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FResultBinding.inflate(layoutInflater)
        val view: View = binding!!.root
        type = requireArguments().getInt("type")
        userService = RxRetrofitBuilder.create(context, UserService::class.java)
        recipeService = RxRetrofitBuilder.create(context, RecipeService::class.java)
        searchListAdapter = if (type == VIEWHOLDER_ITEM_USER) SearchListAdapter(
            type,
            ArrayList(),
            R.layout.li_follow
        ) else SearchListAdapter(type, ArrayList(), R.layout.li_recipe_result)
        searchListAdapter!!.setEmptyView(
            R.layout.rv_search,
            binding!!.resultRecycler.parent as ViewGroup
        )
        searchListAdapter!!.setOnItemClickListener { adapter: BaseQuickAdapter<*, *>, view1: View?, position: Int ->
            when (adapter.getItemViewType(position)) {
                VIEWHOLDER_ITEM_RECIPE_NAME, VIEWHOLDER_ITEM_INGREDIENT, VIEWHOLDER_ITEM_TAG -> {
                    val intent = Intent(context, RecipeDetailActivity::class.java)
                        .putExtra("recipeID", (adapter.data[position] as Recipe).recipeID)
                    startActivity(intent)
                }
                VIEWHOLDER_ITEM_USER -> {
                    val intent2 = Intent(context, HomeActivity::class.java)
                        .putExtra(
                            "userID",
                            ((adapter.data[position] as SearchResult<*>).getContent() as UserBrief).userID
                        )
                    Utils.log(((adapter.data[position] as SearchResult<*>).getContent() as UserBrief).userID)
                    startActivity(intent2)
                }
            }
        }
        binding!!.resultRecycler.layoutManager = LinearLayoutManager(this.context)
        binding!!.resultRecycler.adapter = searchListAdapter
        return view
    }

    override fun setUserVisibleHint(isVisibleToUser: Boolean) {
        super.setUserVisibleHint(isVisibleToUser)
        if (isVisibleToUser) {
            if (activity != null && (activity as SearchActivity?).getKeyword() != null) {
                if (keyword != null && keyword != (activity as SearchActivity?).getKeyword()) {
                    search()
                } else if (keyword == null && (activity as SearchActivity?).getKeyword() != null) {
                    search()
                }
            }
        }
    }

    fun search() {
        if ((activity as SearchActivity?).getKeyword() != null) {
            keyword = (activity as SearchActivity?).getKeyword()
            loadList()
        }
    }

    fun loadList() {
        if (App.isServerAlive()) {
            if (type == searchListAdapter!!.VIEWHOLDER_ITEM_USER) {
                (activity as BaseActivity?).getCompositeDisposable().add(
                    userService!!.getUserByNickname(keyword)
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe({ response: Response<ArrayList<UserBrief?>?>? ->
                            setUserItem(
                                response!!.body()
                            )
                            searchListAdapter!!.setEmptyView(
                                R.layout.rv_empty_search,
                                binding!!.resultRecycler.parent as ViewGroup
                            )
                        }, RxRetrofitBuilder.defaultConsumer())
                )
            } else {
                val selectedService: Single<Response<ArrayList<Recipe?>?>?>?
                selectedService =
                    if (type == searchListAdapter!!.VIEWHOLDER_ITEM_RECIPE_NAME) recipeService!!.getRecipeByName(
                        keyword,
                        "popular"
                    ) else if (type == searchListAdapter!!.VIEWHOLDER_ITEM_TAG) recipeService!!.getRecipeByTag(
                        keyword,
                        "popular"
                    ) else recipeService!!.getRecipeByIngredient(keyword, "popular")
                (activity as BaseActivity?).getCompositeDisposable().add(
                    selectedService
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe({ response: Response<ArrayList<Recipe?>?>? ->
                            setRecipeItem(
                                response!!.body()
                            )
                            searchListAdapter!!.setEmptyView(
                                R.layout.rv_empty,
                                binding!!.resultRecycler.parent as ViewGroup
                            )
                        }, RxRetrofitBuilder.defaultConsumer())
                )
            }
        } else {
            if (type == VIEWHOLDER_ITEM_USER) {
                val userBriefs = DataGenerator.make<ArrayList<UserBrief?>>(
                    resources,
                    resources.getInteger(R.integer.DATA_TYPE_USER_BRIEF)
                )
                setUserItem(userBriefs)
            } else {
                val recipes = DataGenerator.make<ArrayList<Recipe?>>(
                    resources, resources.getInteger(R.integer.DATE_TYPE_RECIPE)
                )
                setRecipeItem(recipes)
            }
        }
    }

    fun setRecipeItem(recipes: ArrayList<Recipe?>?) {
        val searchResults = ArrayList<SearchResult<*>>()
        for (i in recipes!!.indices) {
            if (i != 0 && i % 4 == 0) searchResults.add(SearchResult<Any>(VIEWHOLDER_AD))
            searchResults.add(SearchResult(type, recipes[i]))
        }
        searchListAdapter!!.setNewData(searchResults)
        searchListAdapter!!.setEmptyView(
            R.layout.rv_empty,
            binding!!.resultRecycler.parent as ViewGroup
        )
    }

    fun setUserItem(userBriefs: ArrayList<UserBrief?>?) {
        val searchResults = ArrayList<SearchResult<*>>()
        for (i in userBriefs!!.indices) {
            if (i != 0 && i % 4 == 0) searchResults.add(SearchResult<Any>(VIEWHOLDER_AD))
            searchResults.add(SearchResult(VIEWHOLDER_ITEM_USER, userBriefs[i]))
        }
        searchListAdapter!!.setNewData(searchResults)
        searchListAdapter!!.setEmptyView(
            R.layout.rv_empty,
            binding!!.resultRecycler.parent as ViewGroup
        )
    }
}