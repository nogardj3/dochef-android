package com.yhjoo.dochef.ui.search

import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.widget.SearchView
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.google.android.gms.ads.MobileAds
import com.google.android.material.tabs.TabLayoutMediator
import com.yhjoo.dochef.Constants
import com.yhjoo.dochef.R
import com.yhjoo.dochef.data.model.Recipe
import com.yhjoo.dochef.data.repository.RecipeRepository
import com.yhjoo.dochef.data.repository.UserRepository
import com.yhjoo.dochef.databinding.SearchActivityBinding
import com.yhjoo.dochef.ui.base.BaseActivity
import com.yhjoo.dochef.ui.recipe.RecipeDetailActivity

class SearchActivity : BaseActivity() {
    private val searchTabs = arrayOf(
        Pair("유저", ResultUserFragment()),
        Pair("레시피", ResultRecipeNameFragment()),
        Pair("재료", ResultIngredientFragment()),
        Pair("태그", ResultTagFragment()),
    )

    private val binding: SearchActivityBinding by lazy {
        DataBindingUtil.setContentView(this, R.layout.search_activity)
    }
    private val searchViewModel: SearchViewModel by viewModels {
        SearchViewModelFactory(
            UserRepository(applicationContext),
            RecipeRepository(applicationContext)
        )
    }
    private lateinit var viewPagerAdapter: SearchViewPagerAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        MobileAds.initialize(this)

        binding.apply {
            viewPagerAdapter = SearchViewPagerAdapter(this@SearchActivity)

            searchViewpager.also {
                it.offscreenPageLimit = 4
                it.adapter = viewPagerAdapter
            }

            TabLayoutMediator(searchTablayout, searchViewpager) { tab, position ->
                tab.text = searchTabs[position].first
            }.attach()

            searchSearchview.also {
                it.isSubmitButtonEnabled = true
                it.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
                    override fun onQueryTextChange(newText: String?): Boolean {
                        return false
                    }

                    override fun onQueryTextSubmit(query: String?): Boolean {
                        searchViewModel.searchStart(query ?: "")
                        hideKeyboard(searchSearchview)
                        return true
                    }
                })
            }
        }
    }

    fun goDetail(item: Recipe) {
        startActivity(
            Intent(this, RecipeDetailActivity::class.java)
                .putExtra(Constants.INTENTNAME.RECIPE_ID, item.recipeID)
        )
    }

    inner class SearchViewPagerAdapter(fragmentActivity: FragmentActivity) :
        FragmentStateAdapter(fragmentActivity) {
        override fun getItemCount(): Int {
            return searchTabs.size
        }

        override fun createFragment(position: Int): Fragment {
            return searchTabs[position].second
        }
    }
}