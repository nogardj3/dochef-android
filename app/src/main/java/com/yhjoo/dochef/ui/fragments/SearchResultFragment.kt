package com.yhjoo.dochef.ui.fragments

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.chad.library.adapter.base.BaseQuickAdapter
import com.yhjoo.dochef.R
import com.yhjoo.dochef.databinding.FResultBinding
import com.yhjoo.dochef.model.Recipe
import com.yhjoo.dochef.model.SearchResult
import com.yhjoo.dochef.model.UserBrief
import com.yhjoo.dochef.ui.activities.HomeActivity
import com.yhjoo.dochef.ui.activities.RecipeDetailActivity
import com.yhjoo.dochef.ui.adapter.SearchListAdapter
import com.yhjoo.dochef.utilities.RetrofitBuilder
import com.yhjoo.dochef.utilities.RetrofitServices.RecipeService
import com.yhjoo.dochef.utilities.RetrofitServices.UserService
import com.yhjoo.dochef.utilities.Utils
import java.util.*

class SearchResultFragment : Fragment() {
    object VIEWHOLDER {
        const val AD = 0
        const val ITEM_USER = 1
        const val ITEM_RECIPE_NAME = 2
        const val ITEM_INGREDIENT = 3
        const val ITEM_TAG = 4
    }

    private lateinit var binding: FResultBinding
    private lateinit var userService: UserService
    private lateinit var recipeService: RecipeService
    private lateinit var searchListAdapter: SearchListAdapter
    private var keyword: String? = null
    private var type = 0

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FResultBinding.inflate(layoutInflater)
        val view: View = binding.root

        userService = RetrofitBuilder.create(requireContext(), UserService::class.java)
        recipeService = RetrofitBuilder.create(requireContext(), RecipeService::class.java)

        type = requireArguments().getInt("type")

        binding.apply{
            searchListAdapter =
                if (type == VIEWHOLDER.ITEM_USER)
                    SearchListAdapter(
                        type,
                        ArrayList(),
                        R.layout.li_follow
                    )
                else
                    SearchListAdapter(type, ArrayList(), R.layout.li_recipe_result)
            searchListAdapter.apply{
                setEmptyView(
                    R.layout.rv_search,
                    binding.resultRecycler.parent as ViewGroup
                )
                setOnItemClickListener { adapter: BaseQuickAdapter<*, *>, _: View?, position: Int ->
                    when (adapter.getItemViewType(position)) {
                        VIEWHOLDER.ITEM_RECIPE_NAME, VIEWHOLDER.ITEM_INGREDIENT, VIEWHOLDER.ITEM_TAG -> {
                            val intent = Intent(context, RecipeDetailActivity::class.java)
                                .putExtra("recipeID", (adapter.data[position] as Recipe).recipeID)
                            startActivity(intent)
                        }
                        VIEWHOLDER.ITEM_USER -> {
                            val intent2 = Intent(context, HomeActivity::class.java)
                                .putExtra(
                                    "userID",
                                    ((adapter.data[position] as SearchResult<*>).content as UserBrief).userID
                                )
                            Utils.log(((adapter.data[position] as SearchResult<*>).content as UserBrief).userID)
                            startActivity(intent2)
                        }
                    }
                }
            }
            resultRecycler.apply{
                layoutManager = LinearLayoutManager(requireContext())
                adapter = searchListAdapter
            }
        }

        return view
    }

//    override fun setUserVisibleHint(isVisibleToUser: Boolean) {
//        super.setUserVisibleHint(isVisibleToUser)
//        if (isVisibleToUser) {
//            if (activity != null && (requireActivity() as SearchActivity). .keyword != null) {
//                if (keyword != null && keyword != (activity as SearchActivity).keyword) {
//                    search()
//                } else if (keyword == null && (activity as SearchActivity).keyword != null) {
//                    search()
//                }
//            }
//        }
//    }
//
    fun search() {
//        if ((activity as SearchActivity?).getKeyword() != null) {
//            keyword = (activity as SearchActivity?).getKeyword()
//            loadList()
//        }
    }
//
//    fun loadList() {
//        if (App.isServerAlive) {
//            if (type == searchListAdapter.VIEWHOLDER.ITEM_USER) {
//                (activity as BaseActivity?).compositeDisposable.add(
//                    userService.getUserByNickname(keyword)
//                        .observeOn(AndroidSchedulers.mainThread())
//                        .subscribe({
//                            setUserItem(
//                                it.body()!!
//                            )
//                            searchListAdapter.setEmptyView(
//                                R.layout.rv_empty_search,
//                                binding.resultRecycler.parent as ViewGroup
//                            )
//                        }, RxRetrofitBuilder.defaultConsumer())
//                )
//            } else {
//                val selectedService: Single<Response<ArrayList<Recipe?>?>?>?
//                selectedService =
//                    if (type == searchListAdapter.VIEWHOLDER_ITEM_RECIPE_NAME)
//                        recipeService!!.getRecipeByName(
//                            keyword,
//                            "popular"
//                        )
//                    else if (type == searchListAdapter.VIEWHOLDER_ITEM_TAG)
//                        recipeService!!.getRecipeByTag(
//                            keyword,
//                            "popular"
//                        )
//                    else
//                        recipeService!!.getRecipeByIngredient(
//                            keyword,
//                            "popular"
//                        )
//                (activity as BaseActivity).compositeDisposable.add(
//                    selectedService
//                        .observeOn(AndroidSchedulers.mainThread())
//                        .subscribe({
//                            setRecipeItem(
//                                it.body()!!
//                            )
//                            searchListAdapter.setEmptyView(
//                                R.layout.rv_empty,
//                                binding.resultRecycler.parent as ViewGroup
//                            )
//                        }, RxRetrofitBuilder.defaultConsumer())
//                )
//            }
//        } else {
//            if (type == VIEWHOLDER.ITEM_USER) {
//                val userBriefs = DataGenerator.make<ArrayList<UserBrief?>>(
//                    resources,
//                    resources.getInteger(R.integer.DATA_TYPE_USER_BRIEF)
//                )
//                setUserItem(userBriefs)
//            } else {
//                val recipes = DataGenerator.make<ArrayList<Recipe?>>(
//                    resources, resources.getInteger(R.integer.DATE_TYPE_RECIPE)
//                )
//                setRecipeItem(recipes)
//            }
//        }
//    }
//
//    fun setRecipeItem(recipes: ArrayList<Recipe?>?) {
//        val searchResults = ArrayList<SearchResult<*>>()
//        for (i in recipes!!.indices) {
//            if (i != 0 && i % 4 == 0) searchResults.add(SearchResult<Any>(VIEWHOLDER_AD))
//            searchResults.add(SearchResult(type, recipes[i]))
//        }
//        searchListAdapter.setNewData(searchResults as List<SearchResult<*>?>?)
//        searchListAdapter.setEmptyView(
//            R.layout.rv_empty,
//            binding.resultRecycler.parent as ViewGroup
//        )
//    }
//
//    fun setUserItem(userBriefs: ArrayList<UserBrief?>?) {
//        val searchResults = ArrayList<SearchResult<*>>()
//        for (i in userBriefs!!.indices) {
//            if (i != 0 && i % 4 == 0) searchResults.add(SearchResult<Any>(VIEWHOLDER_AD))
//            searchResults.add(SearchResult(VIEWHOLDER.ITEM_USER, userBriefs[i]))
//        }
//        searchListAdapter.setNewData(searchResults as List<SearchResult<*>?>?)
//        searchListAdapter.setEmptyView(
//            R.layout.rv_empty,
//            binding.resultRecycler.parent as ViewGroup
//        )
//    }
}